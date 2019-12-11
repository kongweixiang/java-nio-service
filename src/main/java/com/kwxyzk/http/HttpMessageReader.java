/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.http;

import com.kwxyzk.context.KSocket;
import com.kwxyzk.message.IMessageReader;
import com.kwxyzk.message.Message;
import com.kwxyzk.message.MessageBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author kongweixiang
 * @date 2019/11/6
 * @since 1.0.0
 */
public class HttpMessageReader implements IMessageReader {
    private List<Message> completeMessages = new CopyOnWriteArrayList<Message>();
    private MessageBuffer messageBuffer;
    private volatile Message nextMessage;

    public void init(MessageBuffer messageBuffer) {
        this.messageBuffer = messageBuffer;
        this.nextMessage = messageBuffer.allocateMessage();
        this.nextMessage.setMetaData(new HttpHeaders());
    }

    public void read(KSocket socket, ByteBuffer byteBuffer) throws IOException {
        byteBuffer.clear();
        byteBuffer.rewind();
        int read = socket.read(byteBuffer);
        byteBuffer.flip();
        this.nextMessage.writeToMessage(byteBuffer);

        int endIndex = HttpUtil.parseHttpRequest(this.nextMessage.getShareArray(), this.nextMessage.getOffset(), this.nextMessage.getOffset() + this.nextMessage.getLength(), (HttpHeaders) this.nextMessage.metaData);
        if(endIndex != -1){
            Message message = this.messageBuffer.allocateMessage();
            message.setMetaData(new HttpHeaders());

            message.writePartialMessageToMessage(nextMessage, endIndex);

            completeMessages.add(nextMessage);
            nextMessage = message;
        }
        byteBuffer.clear();
    }

    public List<Message> getMessages() {
        return completeMessages;
    }

    @Override
    public void close() {
        for (Message message : completeMessages) {
            message.clear();
        }
        this.nextMessage.clear();
        this.completeMessages.clear();
    }
}
