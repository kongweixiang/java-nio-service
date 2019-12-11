/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.http;

import com.kwxyzk.message.IMessageReader;
import com.kwxyzk.message.IMessageReaderFactory;

/**
 * @author kongweixiang
 * @date 2019/11/6
 * @since 1.0.0
 */
public class HttpMessageReaderFactory implements IMessageReaderFactory {
    public IMessageReader createMessageReader() {
        return new HttpMessageReader();
    }
}
