/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.reactor;

import com.kwxyzk.Exception.ServiceException;
import com.kwxyzk.http.HttpMessageReaderFactory;
import com.kwxyzk.http.HttpMessageWriterFactory;
import com.kwxyzk.message.MessageBuffer;
import lombok.Getter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

/**
 * @author kongweixiang
 * @date 2019/11/21
 * @since 1.0.0
 */
@Getter
public class ServerBootstrap {
    private NioReactorGroup boss;
    private NioReactorGroup work;
    private ReadProcessor readProcessor;
    private WriteProcessor writeProcessor;
    private SocketAccepter  socketAccepter  = null;
    private HttpMessageReaderFactory httpMessageReaderFactory = new HttpMessageReaderFactory();
    private HttpMessageWriterFactory httpMessageWriterFactory = new HttpMessageWriterFactory();
    private MessageBuffer messageBuffer = new MessageBuffer();


    private int tcpPort;
    private ServerSocketChannel serverSocketChannel = null;

    public ServerBootstrap(NioReactorGroup boss, NioReactorGroup work) {
        this.boss = boss;
        this.work = work;

    }

    public ServerBootstrap addReadHandle(ProcessorHandle processorHandle) {
        this.readProcessor.addHandle(processorHandle);
        return this;
    }
    public ServerBootstrap writeReadHandle(ProcessorHandle processorHandle) {
        this.readProcessor.addHandle(processorHandle);
        return this;
    }

    public ServerBootstrap setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
        return this;
    }

    public void start() {
        if (this.boss == null || this.work == null) {
            throw new ServiceException();
        }
        boss.serverBootstrap(this);
        work.serverBootstrap(this);
        try {
            this.serverSocketChannel = ServerSocketChannel.open().bind(new InetSocketAddress(this.tcpPort));
            this.serverSocketChannel.configureBlocking(false);
            this.boss.bossEventLoop();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException();
        }
    }


    public ServerBootstrap setBoss(NioReactorGroup boss) {
        this.boss = boss;
        return this;
    }

    public ServerBootstrap setWork(NioReactorGroup work) {
        this.work = work;
        return this;
    }

    public ServerBootstrap setReadProcessor(ReadProcessor readProcessor) {
        this.readProcessor = readProcessor;
        return this;
    }

    public ServerBootstrap setWriteProcessor(WriteProcessor writeProcessor) {
        this.writeProcessor = writeProcessor;
        return this;
    }

    public ServerBootstrap setSocketAccepter(SocketAccepter socketAccepter) {
        this.socketAccepter = socketAccepter;
        return this;
    }

    public ServerBootstrap setHttpMessageReaderFactory(HttpMessageReaderFactory httpMessageReaderFactory) {
        this.httpMessageReaderFactory = httpMessageReaderFactory;
        return this;
    }

    public ServerBootstrap setHttpMessageWriterFactory(HttpMessageWriterFactory httpMessageWriterFactory) {
        this.httpMessageWriterFactory = httpMessageWriterFactory;
        return this;
    }

}
