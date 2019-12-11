package com.kwxyzk.context;

import com.kwxyzk.message.IMessageReader;
import com.kwxyzk.message.IMessageWriter;
import com.kwxyzk.reactor.ReadProcessor;
import com.kwxyzk.reactor.SocketProcessor;
import com.kwxyzk.reactor.WriteProcessor;
import lombok.Data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * request socket
 * @author kongweixiang
 * @date 2019/11/5
 * @since 1.0.0
 */
@Data
public class KSocket {
    private int id;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private SocketChannel socketChannel;

    private IMessageReader messageReader;
    private IMessageWriter messageWriter;

    private ReadProcessor readProcessor;
    private WriteProcessor writeProcessor;
    private SocketProcessor socketProcessor;
    private boolean endOfStreamReached = false;

    public KSocket(SocketChannel socketChannel) {
        this.id = atomicInteger.incrementAndGet();
        this.socketChannel = socketChannel;
    }

    public IMessageReader getMessageReader() {
        return messageReader;
    }

    public KSocket setMessageReader(IMessageReader messageReader) {
        this.messageReader = messageReader;
        return this;
    }

    public IMessageWriter getMessageWriter() {
        return messageWriter;
    }

    public KSocket setMessageWriter(IMessageWriter messageWriter) {
        this.messageWriter = messageWriter;
        return this;
    }

    public int read(ByteBuffer byteBuffer) throws IOException {
        int bytesRead = this.socketChannel.read(byteBuffer);
        int totalBytesRead = bytesRead;

        while(bytesRead > 0){
            bytesRead = this.socketChannel.read(byteBuffer);
            totalBytesRead += bytesRead;
        }
        if(bytesRead == -1){
            this.endOfStreamReached = true;
        }

        return totalBytesRead;
    }

    public int write(ByteBuffer byteBuffer) throws IOException{
        int bytesWritten      = this.socketChannel.write(byteBuffer);
        int totalBytesWritten = bytesWritten;

        while(bytesWritten > 0 && byteBuffer.hasRemaining()){
            bytesWritten = this.socketChannel.write(byteBuffer);
            totalBytesWritten += bytesWritten;
        }

        return totalBytesWritten;
    }


    public void changeToWrite(Selector selector) throws IOException {
        SelectionKey register = this.socketChannel.register(selector, SelectionKey.OP_WRITE);
        register.attach(this);
    }
}
