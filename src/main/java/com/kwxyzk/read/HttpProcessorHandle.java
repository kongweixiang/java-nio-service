/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.read;

import com.kwxyzk.message.Message;
import com.kwxyzk.reactor.ProcessorHandle;

/**
 * @author kongweixiang
 * @date 2019/11/21
 * @since 1.0.0
 */
public class HttpProcessorHandle extends ProcessorHandle {
    @Override
    public Message process(Message request) {
        System.out.println("Message Received from socket: " + request.getSocketId() + new String(request.getShareArray(),request.getOffset(),request.getLength()));
        return request;
    }
}
