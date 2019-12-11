/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.message;

/**
 * @author kongweixiang
 * @date 2019/11/5
 * @since 1.0.0
 */
public class MessageBuffer {

    public static int KB = 1024;
    public static int MB = 1024 * KB;

    private static final int CAPACITY_SMALL  =   4  * KB;
    private static final int CAPACITY_MEDIUM = 128  * KB;
    private static final int CAPACITY_LARGE  = 1024 * KB;

    byte[]  smallMessageBuffer  = new byte[1024 *   4 * KB];   //1024 x   4KB messages =  4MB.
    byte[]  mediumMessageBuffer = new byte[128  * 128 * KB];   // 128 x 128KB messages = 16MB.
    byte[]  largeMessageBuffer  = new byte[16   *   1 * MB];   //  16 *   1MB messages = 16MB.

    BlockQueueIntPointer smallMessageBufferFreeBlocks = new BlockQueueIntPointer(1024);
    BlockQueueIntPointer mediumMessageBufferFreeBlocks = new BlockQueueIntPointer(128);
    BlockQueueIntPointer largeMessageBufferFreeBlocks = new BlockQueueIntPointer(16);

    public MessageBuffer() {
        for (int i = 0; i < smallMessageBufferFreeBlocks.getCapacity(); i++) {
            int offset = i * CAPACITY_SMALL;
            smallMessageBufferFreeBlocks.add(offset);
        }
        for (int i = 0; i < mediumMessageBufferFreeBlocks.getCapacity(); i++) {
            int offset = i * CAPACITY_MEDIUM;
            smallMessageBufferFreeBlocks.add(offset);
        }
        for (int i = 0; i < largeMessageBufferFreeBlocks.getCapacity(); i++) {
            int offset = i * CAPACITY_SMALL;
            smallMessageBufferFreeBlocks.add(offset);
        }
    }

    public Message allocateMessage() {
        int nextFreeBlock = smallMessageBufferFreeBlocks.poll();
        if (nextFreeBlock != -1) {
            Message message = Message.builder()
                    .messageBuffer(this)
                    .shareArray(this.smallMessageBuffer)
                    .capacity(CAPACITY_SMALL)
                    .offset(nextFreeBlock)
                    .length(0)
                    .build();

            return message;
        }
        return null;
    }

    public boolean expendMessage(Message message) {
        if (message.getCapacity() == CAPACITY_SMALL) {
            return moveMessage(message,this.smallMessageBufferFreeBlocks,this.mediumMessageBufferFreeBlocks,this.mediumMessageBuffer,CAPACITY_MEDIUM);
        }else if (message.getCapacity() == CAPACITY_LARGE) {
            return moveMessage(message,this.mediumMessageBufferFreeBlocks,this.largeMessageBufferFreeBlocks,this.largeMessageBuffer,CAPACITY_LARGE);
        }
        return false;
    }

    private boolean moveMessage(Message message, BlockQueueIntPointer srcFreeBlocks, BlockQueueIntPointer descBufferFreeBlocks, byte[] descMessageBuffer, int descCapacity) {
        int nextFreeBlock = descBufferFreeBlocks.poll();
        if(nextFreeBlock == -1) return false;
        System.arraycopy(message.getShareArray(), message.getOffset(), descMessageBuffer, nextFreeBlock, message.getLength());
        srcFreeBlocks.add(message.getOffset());

        message.setShareArray(descMessageBuffer);
        message.setCapacity(descCapacity);
        message.setOffset(nextFreeBlock);
        return true;
    }

    public void clearMessage(Message message) {
        BlockQueueIntPointer srcFreeBlocks = this.smallMessageBufferFreeBlocks;
        if (message.getCapacity() == CAPACITY_SMALL) {
            srcFreeBlocks = this.mediumMessageBufferFreeBlocks;
        }else if (message.getCapacity() == CAPACITY_LARGE) {
            srcFreeBlocks = this.largeMessageBufferFreeBlocks;
        }
        srcFreeBlocks.add(message.getOffset());
    }
}
