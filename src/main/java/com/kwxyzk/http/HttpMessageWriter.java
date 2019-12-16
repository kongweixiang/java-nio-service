/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.http;

import com.kwxyzk.context.KSocket;
import com.kwxyzk.message.IMessageWriter;
import com.kwxyzk.message.Message;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author kongweixiang
 * @date 2019/11/6
 * @since 1.0.0
 */
public class HttpMessageWriter implements IMessageWriter {

    private List<Message> responseMessages = new CopyOnWriteArrayList<>();

    private volatile AtomicBoolean closeStatus = new AtomicBoolean(false);

    @Override
    public int write(String message, KSocket socket) throws IOException {
        return this.write(socket, ByteBuffer.wrap(message.getBytes("utf-8")));
    }

    @Override
    public int write(byte[] bytes, KSocket socket) throws IOException {
        return this.write(socket, ByteBuffer.wrap(bytes));
    }

    @Override
    public int write(KSocket socket, ByteBuffer byteBuffer) throws IOException {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 38\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
//                "<html><body> " + new String(bytes,0,bytes.length)+
                "<html><body> Hello World! </body></html>";
        byteBuffer.clear();
        byteBuffer.rewind();
        byteBuffer.put(httpResponse.getBytes("utf-8"));
        byteBuffer.flip();
    return socket.write(byteBuffer);
    }

    @Override
    public List<Message> getMessages() {
        return this.responseMessages;
    }

    @Override
    public void addMessage(Message response) {
        this.responseMessages.add(response);
    }

    @Override
    public void close() {
        if (closeStatus.compareAndSet(false, true)) {
            for (Message message : responseMessages) {
                message.clear();
            }
            this.responseMessages.clear();
        }
    }
}
