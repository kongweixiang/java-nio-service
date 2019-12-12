/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.service;

import com.kwxyzk.http.HttpMessageReaderFactory;
import com.kwxyzk.http.HttpMessageWriterFactory;
import com.kwxyzk.reactor.*;
import com.kwxyzk.read.HttpProcessorHandle;
import com.kwxyzk.write.WriteHandle;

/**
 * @author kongweixiang
 * @date 2019/12/10
 * @since 1.0.0
 */
public class Server {

    public static void main(String[] args) {
        NioReactorGroup boss = new NioReactorGroup(2,5,"nio-boss");
        NioReactorGroup work = new NioReactorGroup(5,10,"nio-work");
        ReadProcessor readProcessor = new ReadProcessor();
        readProcessor.addHandle(new HttpProcessorHandle());
        WriteProcessor writeProcessor = new WriteProcessor();
        writeProcessor.addHandle(new WriteHandle());
        ServerBootstrap serverBootstrap = new ServerBootstrap(boss,work);
        serverBootstrap.setTcpPort(9999)
                .setHttpMessageReaderFactory(new HttpMessageReaderFactory())
                .setHttpMessageWriterFactory(new HttpMessageWriterFactory())
                .setReadProcessor(readProcessor)
                .setWriteProcessor(writeProcessor)
                .start();
    }
}
