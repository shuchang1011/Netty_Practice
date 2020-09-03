package cn.com.agree.netty.tcp;/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

/**
 * 客户端处理器
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/30 23:48
 */

public class MyClientHandler extends SimpleChannelInboundHandler<MyMessageProtocal> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MyMessageProtocal myMessageProtocal) throws Exception {
        System.out.println(new String(myMessageProtocal.getContent(), Charset.forName("UTF-8")));
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString();
        System.out.println("已连接上服务端" + remoteAddress);
        //通道准备就绪后，发送十条消息，测试tcp粘包问题是否解决
        for (int i = 0; i < 10; i++) {
            MyMessageProtocal messageProtocal = new MyMessageProtocal();
            byte[] message = new byte[messageProtocal.getLength()];
            message = "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh".getBytes();
            messageProtocal.setLength(message.length);
            messageProtocal.setContent(message);
            ctx.writeAndFlush(messageProtocal);
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
