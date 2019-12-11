/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.reactor;

import com.kwxyzk.message.Message;

import java.util.HashSet;
import java.util.Set;

/**
 * @author kongweixiang
 * @date 2019/11/5
 * @since 1.0.0
 */
public class ReadProcessor {
    private Set<ProcessorHandle> inHandles = new HashSet<ProcessorHandle>();


    public void addHandle(ProcessorHandle inHandle) {
        this.inHandles.add(inHandle);
    }


    public Message processor(Message message) {
        for (ProcessorHandle inHandle : inHandles) {
            message = inHandle.process(message);
        }
        return message;
    }

}
