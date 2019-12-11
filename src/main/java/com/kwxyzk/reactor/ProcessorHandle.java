/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.reactor;

import com.kwxyzk.message.Message;
import com.kwxyzk.message.MessageBuffer;

/**
 * @author kongweixiang
 * @date 2019/11/5
 * @since 1.0.0
 */
public abstract class ProcessorHandle implements Comparable<ProcessorHandle>{
    protected Integer order = 0;

    public abstract Message process(Message request);

    public int compareTo(ProcessorHandle o) {
        return o.getOrder().compareTo(this.getOrder());
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
