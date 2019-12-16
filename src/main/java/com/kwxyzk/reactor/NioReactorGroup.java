/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.reactor;

import com.kwxyzk.context.KSocket;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * @author kongweixiang
 * @date 2019/11/21
 * @since 1.0.0
 */
public class NioReactorGroup extends ThreadPoolExecutor implements ExecutorService{
    private static final int coreSize = 10;

    private static final int maxSize = 50;

    private static final long timeOut = 10 * 10000L;


    private ServerBootstrap serverBootstrap;

    private static Integer capacity = 1000;


    private BlockingDeque<IOFuture> taskPointer = new LinkedBlockingDeque<>(capacity);

    public NioReactorGroup(String name) {
        super(coreSize, maxSize,
                timeOut, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(capacity),
                new NioThreadFactory(name, name));
    }

    public NioReactorGroup(int coreSize, String name) {
        super(coreSize, maxSize,
                timeOut, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(capacity),
                new NioThreadFactory(name, name));
    }

    public NioReactorGroup(int coreSize, int maxSize, String name) {
        super(coreSize, maxSize,
                timeOut, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(capacity),
                new NioThreadFactory(name, name));
    }

    public NioReactorGroup(int coreSize, int maxSize, long timeOut, String name) {
        super(coreSize, maxSize,
                timeOut, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(capacity),
                new NioThreadFactory(name, name));
    }

    public NioReactorGroup serverBootstrap(ServerBootstrap serverBootstrap) {
        this.serverBootstrap = serverBootstrap;
        return this;
    }

    public void addEvent(KSocket kSocket) throws IOException {
        System.out.println("总任务："+this.getTaskCount()+" 已完成："+this.getCompletedTaskCount());
        while (true) {
            IOFuture task = taskPointer.poll();
            if (task == null) {
                task = new IOFuture(new SocketProcessor(), this.taskPointer);
                if (isShutdown()) {
                    getRejectedExecutionHandler().rejectedExecution(task, this);
                } else {
                    if (isShutdown() &&
                            remove(task))
                        task.cancel(false);
                    else {
                        if (task.isCancelled() || !task.add(kSocket)) {
                            continue;
                        }
                        if (getPoolSize() <= getMaximumPoolSize()) {
                            execute(task);
                        } else {
                            super.getQueue().add(task);
                        }
                    }

                }
            } else if (!isShutdown()) {
                if (task.add(kSocket)) {
                    break;
                }
//                else {
//                    remove(task);
//                    task.cancel(false);
//                }

            } else {
                System.out.println("系统已关闭——————");
            }
        }

    }


    public void workEventLoop() throws IOException {
        super.prestartCoreThread();
    }
    public void bossEventLoop() throws IOException {
        for (int i = 0; i < this.getMaximumPoolSize(); i++) {
            SocketAccepter socketAccepter = new SocketAccepter(serverBootstrap);
            this.submit(socketAccepter);
        }
        while (true) {
            if(this.getPoolSize() < this.getMaximumPoolSize()) {
                SocketAccepter socketAccepter = new SocketAccepter(serverBootstrap);
                this.submit(socketAccepter);
            }
        }
    }
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        System.out.println("task run");
        if (r instanceof IOFuture) {
            IOFuture ioFuture = (IOFuture) r;
            System.out.println(ioFuture.getName());
        }
    }

    protected void afterExecute(Runnable r, Throwable t) {
        System.out.println("task end"+r+t);
        if (r instanceof IOFuture) {
            IOFuture ioFuture = (IOFuture) r;
            ioFuture.close();
            System.out.println(ioFuture.getName());
        }
    }

}
