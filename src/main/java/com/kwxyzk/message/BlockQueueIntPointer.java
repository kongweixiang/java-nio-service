/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.message;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author kongweixiang
 * @date 2019/11/5
 * @since 1.0.0
 */
public class BlockQueueIntPointer {
    private int readPos;
    private int writePos;
    private boolean flipped = false;
    private int element[];

    private int capacity = 0;

    private Lock lock = new ReentrantLock();

    public BlockQueueIntPointer(int capacity) {
        this.element = new int[capacity];
        this.capacity = capacity;
    }

    public int poll(){
        lock.lock();
        int i = doPoll();
        lock.unlock();
        return i;
    }

    private int doPoll() {
        if (!flipped) {
            if (readPos < writePos) {
                return element[readPos++];
            }else {
                return -1;
            }
        }else {
            if (readPos == capacity) {
                readPos = 0;
                flipped = false;
                return readPos < writePos ? element[readPos++] : -1;
            }else {
                return element[readPos++];
            }
        }
    }

    public boolean add(int element) {
        lock.lock();
        boolean b = doAdd(element);
        lock.unlock();
        return b;
    }

    private boolean doAdd(int element) {
        if (!flipped) {
            if (writePos == capacity) {
                flipped = true;
                writePos = 0;
                if (writePos < readPos) {
                    this.element[writePos++] = element;
                    return true;
                }else{
                    return false;
                }
            }else {
                this.element[writePos++] = element;
                return true;
            }

        } else{
            if (writePos < readPos) {
                this.element[writePos++] = element;
                return true;
            }else {
                return false;
            }
        }
    }

    public int getCapacity() {
        return this.capacity;
    }
}
