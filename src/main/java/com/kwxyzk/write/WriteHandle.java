/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.write;

import com.kwxyzk.message.Message;
import com.kwxyzk.message.MessageBuffer;
import com.kwxyzk.reactor.ProcessorHandle;

import java.nio.channels.SocketChannel;

/**
 * @author kongweixiang
 * @date 2019/11/5
 * @since 1.0.0
 */
public class WriteHandle extends ProcessorHandle {

    @Override
    public Message process(Message request) {
        return request;
    }
}
