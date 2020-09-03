package cn.com.agree.netty;

import cn.com.agree.netty.tcp.MyMessageProtocal;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
/**
 * 职责描述
 *
 * <pre>
 *
 * 代码调用示例
 *
 * </pre>
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/27 16:07
 */

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    //客户端建立连接，通道已就绪
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("执行client-channelActive");
        Channel channel = ctx.channel();
        channel.writeAndFlush(Unpooled.copiedBuffer("客户端：" + channel.remoteAddress() + "已连接服务器", CharsetUtil.UTF_8));
        for (int i = 0; i < 10; i++) {
            ctx.writeAndFlush(Unpooled.copiedBuffer("hello,server,i am " + i, CharsetUtil.UTF_8));
        }
    }

    //客户端read事件处理
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("执行client-channelRead");
        ByteBuf byteBuf = (ByteBuf) msg;
        Channel channel = ctx.channel();
        System.out.println("服务器端-" + channel.remoteAddress() + "回复消息：" + byteBuf.toString(CharsetUtil.UTF_8));
    }

    //客户端read事件完成后的处理
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("执行client-channelReadComplete");
        ctx.writeAndFlush("客户端已收到消息");
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("执行client-exceptionCaught");
        System.out.println(cause);
        ctx.close();
    }
}
