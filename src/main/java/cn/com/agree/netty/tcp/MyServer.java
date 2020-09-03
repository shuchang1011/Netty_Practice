/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
package cn.com.agree.netty.tcp;

import cn.com.agree.netty.NettyServerHandler;
import cn.com.agree.netty.chat.NettyChatServerHandler;
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
 * 创建一个服务端
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/28 17:17
 */

public class MyServer {

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
                    .childOption(ChannelOption.SO_KEEPALIVE, true)  //客户端建立连接之后，如果超过一段时间没有接收到客户端的请求，就会发送一个ack检测包去探测
                                                                            //如果设置为false的话，客户端宕机了，服务端也不知道客户端的情况，仍然保留失效的连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MyMessageDecoder());
                            socketChannel.pipeline().addLast(new MyMessageEncoder());
                            socketChannel.pipeline().addLast(new MyServerHandler());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(6668); //绑定端口号

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //shutdownGracefully方法能够优雅停机，等待所有执行中的任务执行完成，并不再接收新的任务
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
