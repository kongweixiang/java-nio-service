/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.reactor;

import com.kwxyzk.context.KSocket;
import com.kwxyzk.context.WorkContext;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author kongweixiang
 * @date 2019/11/24
 * @since 1.0.0
 */
public class IOFuture<V> extends FutureTask<V> {
    private Selector selector;
    private AtomicInteger count = new AtomicInteger(0);
    private static AtomicInteger num = new AtomicInteger(0);
    private BlockingDeque<IOFuture> taskPointer;
    private SocketProcessor socketProcessor;
    private String name = "任务-";

    public IOFuture(SocketProcessor socketProcessor, V result, BlockingDeque<IOFuture> taskPointer) {
        super(socketProcessor, result);
        this.taskPointer = taskPointer;
        this.selector = socketProcessor.getSelector();
        this.name = this.setName();
        try {
            taskPointer.put(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String setName() {
        return this.name+num.incrementAndGet();
    }

    public String getName() {
        return name;
    }

    public IOFuture(SocketProcessor socketProcessor, BlockingDeque<IOFuture> taskPointer) {
        super( Executors.callable(socketProcessor, null));
        this.taskPointer = taskPointer;
        this.selector = socketProcessor.getSelector();
        this.socketProcessor = socketProcessor;
        this.name = this.setName();
        try {
            taskPointer.put(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean add(KSocket t){
        if (this.isCancelled() || this.socketProcessor == null || this.socketProcessor.isInterrupted()) {
            this.cancel(false);
            return false;
        }
        if (count.incrementAndGet() <= WorkContext.socketProcessNum) {
            try {
                SelectionKey register = t.getSocketChannel().register(this.selector, SelectionKey.OP_READ);
                register.attach(t);
                taskPointer.put(this);
            }catch (ClosedChannelException e){
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
                count.decrementAndGet();
                return false;
            }
            return true;
        } else {
            this.socketProcessor.interrupt();
            return false;
        }
    }
}
