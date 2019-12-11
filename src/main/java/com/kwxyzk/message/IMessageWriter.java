/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.message;

import com.kwxyzk.context.KSocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author kongweixiang
 * @date 2019/11/21
 * @since 1.0.0
 */
public interface IMessageWriter {


    int write(String message, KSocket socket)  throws IOException;

    int write(byte[] bytes, KSocket socket)  throws IOException;

    int write(KSocket socket, ByteBuffer byteBuffer) throws IOException;

    List<Message> getMessages();

    void addMessage(Message response);

    void close();
}
