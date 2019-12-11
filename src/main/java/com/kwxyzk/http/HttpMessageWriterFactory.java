/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.http;

import com.kwxyzk.message.IMessageReader;
import com.kwxyzk.message.IMessageReaderFactory;
import com.kwxyzk.message.IMessageWriter;
import com.kwxyzk.message.IMessageWriterFactory;

/**
 * @author kongweixiang
 * @date 2019/11/6
 * @since 1.0.0
 */
public class HttpMessageWriterFactory implements IMessageWriterFactory {

    @Override
    public IMessageWriter createMessageWriter() {
        return new HttpMessageWriter();
    }
}
