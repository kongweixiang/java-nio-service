/*
 * @author kongweixiang
 * @version 1.0.0
 */
package com.kwxyzk.reactor;

import com.kwxyzk.Exception.ServiceException;
import com.kwxyzk.context.KSocket;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author kongweixiang
 * @date 2019/11/21
 * @since 1.0.0
 */
public class SocketAccepter implements Runnable{


    private ServerSocketChannel serverSocketChannel = null;


    private ServerBootstrap serverBootstrap;


    public SocketAccepter(ServerBootstrap serverBootstrap) {
        this.serverSocketChannel = serverBootstrap.getServerSocketChannel();
        this.serverBootstrap = serverBootstrap;
    }

    public void run() {
        while(true) {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    System.out.println(Thread.currentThread().getName()+"收到请求:" + socketChannel.getRemoteAddress().toString());
                    socketChannel.configureBlocking(false);
                    KSocket newSocket = new KSocket(socketChannel);
                    newSocket.setMessageReader(this.serverBootstrap.getHttpMessageReaderFactory().createMessageReader());
                    newSocket.getMessageReader().init(this.serverBootstrap.getMessageBuffer());
                    newSocket.setMessageWriter(this.serverBootstrap.getHttpMessageWriterFactory().createMessageWriter());
                    newSocket.setReadProcessor(this.serverBootstrap.getReadProcessor());
                    newSocket.setWriteProcessor(this.serverBootstrap.getWriteProcessor());
                    this.serverBootstrap.getWork().addEvent(newSocket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
