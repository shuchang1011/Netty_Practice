package cn.com.agree.netty;
/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 构建netty服务端
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/27 9:40
 */

public class NettyServer {

    public static void main(String[] args) {
        //1.构建netty服务端的两个工作线程组：bossGroup和workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);  //单线程工作组
        EventLoopGroup workerGroup = new NioEventLoopGroup();  //默认创建一个含有8个线程的线程池工作组

        try {


            //2.创建服务端启动对象，并进行初始化配置
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)    //首先给启动对象配置相应的工作组
                    .channel(NioServerSocketChannel.class)   //配置通道的实现类
                    .option(ChannelOption.SO_BACKLOG, 128)   //配置ServerSocketChannel的选项参数，SO_BACKLOG定义了客户端等待连接队列的大小
                                                                    //服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)  //child开头的方法,表示处理或配置服务端接收到的对应客户端连接的SocketChannel通道
                                                                            //SO_KEEPALIVE代表客户端是否保持连接，如果长时间没有发送请求，则会发送给客户端一个ack确认包，若无应答，服务端则清掉客户端的连接，否则服务端会一直保持当前的tcp连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {  //定义子工作组（WorkerGroup）的处理器

                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //添加http请求协议的编码/解码器，不添加的话就无法解析http的请求
                            socketChannel.pipeline().addLast(new HttpServerCodec());
                            //pipeline管道通过双向链表实现，承载着多个handler处理器，通过addLast将处理器装载到链表尾部
                            socketChannel.pipeline().addLast(new HttpServerHandler());
                        }
                    });
            //3.绑定启动端口号,并定义一个ChannelFuture对象接收事件监听的结果
            ChannelFuture channelFuture = serverBootstrap.bind(7777).sync();

            //监听通道关闭
            //这句话的作用是，保证主线程在执行玩bind().sync()后，不会立即进入finally代码块，关闭我们的工作组，结束server服务
            //执行这句话后，会让线程进入wait状态，保证nettyServer的持续运行，除非监听到了channel的关闭,否则server服务都能正常运行
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //5.关闭工作组线程
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
