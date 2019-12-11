/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.reactor;

import com.kwxyzk.context.KSocket;
import com.kwxyzk.message.Message;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author kongweixiang
 * @date 2019/11/21
 * @since 1.0.0
 */
public class SocketProcessor implements Runnable{
    private ThreadLocal<ByteBuffer> threadLocal = new ThreadLocal<>();
    private ByteBuffer byteBuffer;
    private Selector readSelector   = null;
    private Selector writerSelector   = null;
    private boolean isInterrupted =false;
    private int timeout = 0;

    public void interrupt() {
        this.isInterrupted = true;
    }

    public boolean isInterrupted() {
        return isInterrupted;
    }

    public SocketProcessor() {
        try {
            this.readSelector = Selector.open();
            this.writerSelector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Selector getSelector() {
        return readSelector;
    }

    @Override
    public void run() {
        this.byteBuffer = threadLocal.get();
        if (this.byteBuffer == null) {
            this.byteBuffer = ByteBuffer.allocate(1024 * 1024);
            threadLocal.set(byteBuffer);
        }
        while(true){
            try {
                executeCycle();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }finally {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean isEmpty = this.readSelector.selectedKeys().size() == 0 && this.writerSelector.selectedKeys().size() == 0;
                if (isEmpty) {
                    timeout++;
                }else {
                    timeout = 0;
                }
                if (isEmpty && (isInterrupted || Thread.currentThread().isInterrupted())) {
                    System.out.println(Thread.currentThread().getName() + "线程开始中断");
                    try {
                        this.readSelector.close();
                        this.writerSelector.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }else {
                    if(timeout >=100){
                        this.isInterrupted = true;
                        break;
                    }
                }
            }
        }
    }

    private void executeCycle() throws IOException {
        if (this.byteBuffer == null) {
            threadLocal.set(ByteBuffer.allocate(1024 * 1024));
        }
        readFromSockets();
        writeToSockets();
    }

    private void writeToSockets() throws IOException {
        int i = this.writerSelector.selectNow();
        if (i > 0) {
            Set<SelectionKey> selectionKeys = this.writerSelector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                KSocket socket = (KSocket) selectionKey.attachment();
                try {
                    writeToSocket(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                iterator.remove();
                selectionKey.cancel();
            }
        }
    }

    private void writeToSocket(KSocket socket) throws IOException {
        List<Message> fullMessages = socket.getMessageWriter().getMessages();
        if(fullMessages.size() > 0){
            for(Message message : fullMessages){
                message.setSocketId(socket.getId());
                Message response = socket.getWriteProcessor().processor(message);
                int offset =0;
                int length;
                int writeLength = 0;
                while (true){
                    length = byteBuffer.capacity() < response.getLength() - writeLength ? byteBuffer.capacity() : message.getLength() - writeLength;
                    if (length == 0) {
                        break;
                    }
                    byteBuffer.clear();
                    byteBuffer.rewind();
                    byteBuffer.put(message.getShareArray(),offset,length);
                    byteBuffer.flip();
                    socket.getMessageWriter().write(socket, byteBuffer);
                    offset += length;
                    writeLength += length;
                }
            }
            socket.getMessageWriter().close();
        }
    }

    private void readFromSockets() throws IOException {
        int i = this.readSelector.selectNow();
        if (i > 0) {
            Set<SelectionKey> selectionKeys = this.readSelector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                try {
                    readFromSocket(selectionKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                iterator.remove();
            }
        }
    }

    private void readFromSocket(SelectionKey selectionKey) throws IOException {
        KSocket socket = (KSocket) selectionKey.attachment();
        socket.getMessageReader().read(socket, this.byteBuffer);

        List<Message> fullMessages = socket.getMessageReader().getMessages();
        if(fullMessages.size() > 0){
            for(Message message : fullMessages){
                message.setSocketId(socket.getId());
                Message msg = socket.getReadProcessor().processor(message);
                socket.getMessageWriter().addMessage(msg);
            }
            fullMessages.clear();
            socket.changeToWrite(this.writerSelector);
        }

        if(socket.isEndOfStreamReached()){
            System.out.println("Socket closed: " + socket.getId());
            selectionKey.attach(null);
            selectionKey.cancel();
            selectionKey.channel().close();
            socket.getMessageReader().close();
        }
    }
}
