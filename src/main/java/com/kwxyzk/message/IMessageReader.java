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
 * @date 2019/11/6
 * @since 1.0.0
 */
public interface IMessageReader {
    void init(MessageBuffer messageBuffer);

    void read(KSocket socket, ByteBuffer byteBuffer) throws IOException;

    List<Message> getMessages();

    void close();
}
