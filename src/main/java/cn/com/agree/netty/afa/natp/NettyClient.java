/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
package cn.com.agree.netty.afa.natp;

import cn.com.agree.netty.afa.natp.codec.NATPDecoder;
import cn.com.agree.netty.afa.natp.codec.NATPEncoder;
import cn.com.agree.netty.tcp.MyClientHandler;
import cn.com.agree.netty.tcp.MyMessageDecoder;
import cn.com.agree.netty.tcp.MyMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.util.CharsetUtil;

import java.util.Scanner;

/**
 * 客户端
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/29 17:37
 */

public class NettyClient extends ChannelInboundHandlerAdapter {

    private static EventLoopGroup loopGroup;

    public static void main(String[] args) {
        loopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.RCVBUF_ALLOCATOR,
                            new AdaptiveRecvByteBufAllocator(128, 1024, 65536))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel nioSocketChannel) throws Exception {
                            //通过DelimiterBasedFrameDecoder对自定义数据包进行截断处理
                            /*ByteBuf delimiter = Unpooled.copiedBuffer("\r\n".getBytes());
                            //maxFrameLength表示单条消息的最大长度，如果达到该长度还没有找到分隔符会抛出异常
                            nioSocketChannel.pipeline().addFirst(new DelimiterBasedFrameDecoder(8096, delimiter));
                            */nioSocketChannel.pipeline().addLast(new NATPDecoder());
                            nioSocketChannel.pipeline().addLast(new NATPEncoder());
                            nioSocketChannel.pipeline().addLast(new NettyClientHandler());
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
