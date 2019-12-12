/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.message;

import com.kwxyzk.Exception.ServiceException;
import lombok.Builder;
import lombok.Data;

import javax.sql.rowset.serial.SerialException;
import java.nio.ByteBuffer;

/**
 * @author kongweixiang
 * @date 2019/11/5
 * @since 1.0.0
 */
@Data
public class Message {

    private MessageBuffer messageBuffer;
    private long socketId;
    private byte[] shareArray;
    private int capacity;
    private int offset = 0;
    private int length = 0;
    public Object metaData    = null;

    public Message() {
    }

    @Builder
    public Message(MessageBuffer messageBuffer, long socketId, byte[] shareArray, int capacity, int offset, int length, Object metaData) {
        this.messageBuffer = messageBuffer;
        this.socketId = socketId;
        this.shareArray = shareArray;
        this.capacity = capacity;
        this.offset = offset;
        this.length = length;
        this.metaData = metaData;
    }


    public int writeToMessage(ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();
        while (remaining > this.capacity - this.length) {
            if (this.messageBuffer.expendMessage(this)) {
                return -1;
            }
        }
        int copyLength = Math.min(remaining, this.capacity - this.length);
        if (this.capacity - this.length < remaining) {
            System.out.println("缓冲空间不够");
        }
//        byteBuffer.get(this.shareArray, this.offset + this.length, copyLength);
        byteBuffer.get(this.shareArray, this.offset + this.length, copyLength);
        this.length += copyLength;
        return remaining;
    }

    public int writeToMessage(String message) {
        byte[] byteArray = message.getBytes();
        return this.writeToMessage(byteArray, 0, byteArray.length);
    }

    public int writeToMessage(byte[] byteArray) {
        return this.writeToMessage(byteArray, 0, byteArray.length);
    }

    private int writeToMessage(byte[] byteArray, int i, int length) {
        while (length > this.capacity - this.length) {
            if (this.messageBuffer.expendMessage(this)) {
                return -1;
            }
        }

        int copyLength = Math.min(length, this.capacity - this.length);
        System.arraycopy(byteArray, 0, this.shareArray, this.offset + this.length, copyLength);
        length += copyLength;
        return length;
    }

    public void writePartialMessageToMessage(Message message, int endIndex) {
        int startIndexPartialMessage = message.getOffset() + endIndex;
        int lengthOfPartialMessage = (message.getOffset() + message.getLength()) - endIndex;
        if (lengthOfPartialMessage < 0) {
            System.out.println("失败");
            throw new ServiceException();
        }
        if (lengthOfPartialMessage > 0) {
            System.arraycopy(message.getShareArray(), startIndexPartialMessage, this.shareArray, this.offset, lengthOfPartialMessage);
        }
    }


    public void clear() {
        messageBuffer.clearMessage(this);
    }
}
