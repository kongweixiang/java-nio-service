/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk;

import com.kwxyzk.http.HttpHeaders;
import com.kwxyzk.http.HttpUtil;
import com.kwxyzk.message.Message;
import com.kwxyzk.message.MessageBuffer;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @author kongweixiang
 * @date 2019/12/12
 * @since 1.0.0
 */
public class MessageTest {



    @Test
    public void makeMessage() throws UnsupportedEncodingException {
        MessageBuffer messageBuffer = new MessageBuffer();
        String httpRequest =
                "GET / HTTP/1.1\r\n" +
                        "Content-Length: 5\r\n" +
                        "\r\n12345" +
                        "GET / HTTP/1.1\r\n" +
                        "Content-Length: 5\r\n" +
                        "\r\n12345";

        byte[] source = httpRequest.getBytes("UTF-8");
        for (int i = 0; i < 512; i++) {
            Message message = messageBuffer.allocateMessage();
            message.setMetaData(new HttpHeaders());
            ByteBuffer byteBuffer = ByteBuffer.wrap(source);
            byteBuffer.clear();
            byteBuffer.rewind();
            message.writeToMessage(byteBuffer);
            int endIndex = HttpUtil.parseHttpRequest(message.getShareArray(), message.getOffset(), message.getOffset() + message.getLength(), (HttpHeaders) message.metaData);
            if(endIndex != -1){
                Message message2 = messageBuffer.allocateMessage();
                message2.setMetaData(new HttpHeaders());
                try {

                    message2.writePartialMessageToMessage(message, endIndex);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
//                message2.clear();
            }
            byteBuffer.clear();
//            message.clear();
        }


    }

}
