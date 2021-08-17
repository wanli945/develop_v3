package com.baibai.nettyserver.core;

import com.baibai.nettyserver.filter.NettyServerInitFilter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Title: NettyServer
 * Description:
 * Netty服务端
 * Version:1.0.0
 *
 * @author wanli
 * @date 2017-5-21
 */
public class nettyDeviceServer {

    private static final int port = 9100; //设置服务端端口

    private static Logger log = LoggerFactory.getLogger(nettyDeviceServer.class);

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(8);// 通过nio方式来接收连接和处理连接
        EventLoopGroup workGroup = new NioEventLoopGroup();
        InetSocketAddress address = new InetSocketAddress(port);
        try {
            ServerBootstrap b = new ServerBootstrap()
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(address)
                    .childHandler(new NettyServerInitFilter())
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.SIMPLE);
            //端口绑定，开始接收进来的连接
            ChannelFuture future = b.bind(port).sync();
            log.info("netty服务器开始监听端口：" + address.getPort());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.info("server服务端异常。。。" + e);
            e.getMessage();
            bossGroup.shutdownGracefully();   //关闭EventLoopGroup，释放掉所有资源包括创建的线程
            workGroup.shutdownGracefully();   //关闭EventLoopGroup，释放掉所有资源包括创建的线程
        }
    }
}
