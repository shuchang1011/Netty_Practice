package cn.com.agree.netty.chat;/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */

import cn.com.agree.netty.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 创建一个netty聊天服务端（负责监听客户端在线情况，接受消息并转发至其他客户端）
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/28 17:17
 */

public class NettyChatServer {

    public static void main(String[] args) {
        //1.构建两个Reactor工作组，bossGroup负责监听accept,workerGroup负责监听read和write
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();  //默认创建最多存在8个线程的线程池

        try {
            //2.创建服务端启动对象，并进行相关配置
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)  //配置服务端工作组
                    .channel(NioServerSocketChannel.class)  //配置服务端通道实现类
                    .option(ChannelOption.SO_BACKLOG, 128)  //配置服务端处理客户端请求的线程数达到峰值时，已经完成tcp三次握手的客户端会进入等待队列等候处理
                                                                    //SO_BACKLOG代表等待队列的大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true)  //客户端连接是否保持心跳
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new StringEncoder());
                            socketChannel.pipeline().addLast(new NettyChatServerHandler());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(6666); //绑定端口号

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
