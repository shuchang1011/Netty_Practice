package cn.com.agree.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/* Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 **/
/*
*
* 构建netty客户端
*
* @author shuchang
* @version 1.0
* @date 2020/8/27 10:56
*
*/


public class NettyClient {

    public static void main(String[] args) {
        EventLoopGroup loopGroup = null;
        try {
            //构建一个客户端的工作组来监听请求
            loopGroup = new NioEventLoopGroup();

            //创建一个客户端的启动器，并进行初始化配置
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyClientHandler());
                        }
                    });

            //连接服务端操作
            InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 7777);
            ChannelFuture future = bootstrap.connect(socketAddress);

            //监听关闭操作
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅的关闭工作组
            loopGroup.shutdownGracefully();
        }
    }
}
