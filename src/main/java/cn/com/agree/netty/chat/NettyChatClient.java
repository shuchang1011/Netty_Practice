package cn.com.agree.netty.chat;
/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.util.Scanner;

/**
 * 聊天客户端
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/29 17:37
 */

public class NettyChatClient extends ChannelInboundHandlerAdapter {

    private static EventLoopGroup loopGroup;

    public static void main(String[] args) {
        loopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(new StringDecoder());
                            nioSocketChannel.pipeline().addLast(new StringEncoder());
                            nioSocketChannel.pipeline().addLast(new NettyChatClientHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6666);

            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNextLine()) {
                channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(scanner.nextLine(), CharsetUtil.UTF_8));
            }

            channelFuture.channel().close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            loopGroup.shutdownGracefully();
        }
    }
}
