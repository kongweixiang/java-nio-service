/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.reactor;

import lombok.Data;

/**
 * @author kongweixiang
 * @date 2019/11/5
 * @since 1.0.0
 */
@Data
public  class IOWork extends Thread{

    public IOWork() {
    }

    public IOWork(ThreadGroup group, Runnable target, String name,
                  long stackSize) {
        super(group, target, name, stackSize);
    }
}
