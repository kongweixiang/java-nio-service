/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.reactor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author kongweixiang
 * @date 2019/11/21
 * @since 1.0.0
 */
public class NioThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public NioThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" +
                poolNumber.getAndIncrement() +
                "-thread-";
    }
    public NioThreadFactory(String groupName,String namePrefix) {
        SecurityManager s = System.getSecurityManager();
        this.group = new ThreadGroup(groupName);
        this.namePrefix = namePrefix +"-" +
                poolNumber.getAndIncrement() +
                "-thread-";
    }

    public IOWork newThread(Runnable r) {
        IOWork t = new IOWork(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        t.setDaemon(true);
        return t;
    }
}
