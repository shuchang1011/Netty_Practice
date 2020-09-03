package cn.com.agree.netty.chat;
/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * 客户端处理器
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/29 17:45
 */

public class NettyChatClientHandler extends ChannelInboundHandlerAdapter {

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString();
        System.out.println("已连接上服务端" + remoteAddress);
        for (int i = 0; i < 10; i++) {
            ctx.writeAndFlush(Unpooled.copiedBuffer("hello,server,i am " + i, CharsetUtil.UTF_8));
        }
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString().substring(1);
        System.out.println("与服务端" + remoteAddress + "断开连接");
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(new String(msg.toString().getBytes(), Charset.forName("UTF-8")));
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("执行client-exceptionCaught");
        cause.printStackTrace();
        //直接关闭管道的连接
        ctx.close();
    }
}
