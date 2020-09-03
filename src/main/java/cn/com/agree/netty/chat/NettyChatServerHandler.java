package cn.com.agree.netty.chat;/*
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

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端处理器
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/29 17:08
 */

public class NettyChatServerHandler extends ChannelInboundHandlerAdapter {

    private static Map<String, Channel> channelMap = new HashMap<String, Channel>();

    //客户端已建立连接，通道处于就绪状态
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("事件channelActive");
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString().substring(1);
        System.out.println("客户端-" + remoteAddress + "已上线");
        //通知其他已连接客户端
        for (String key : channelMap.keySet()) {
            if(!key.equals(remoteAddress)) {
                channelMap.get(key).writeAndFlush(Unpooled.copiedBuffer("客户端-" +
                        channel.remoteAddress() + "上线了", CharsetUtil.UTF_8));
            }
        }
        channelMap.put(remoteAddress, channel);
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("事件channelInactive");
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString().substring(1);
        System.out.println("客户端-" + remoteAddress + "已离线");
        //通知其他已连接客户端
        for (String key : channelMap.keySet()) {
            if(!key.equals(remoteAddress)) {
                channelMap.get(key).writeAndFlush(Unpooled.copiedBuffer("客户端-" +
                        channel.remoteAddress() + "离线了", CharsetUtil.UTF_8));
            }
        }
        channelMap.remove(remoteAddress);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("事件channelRead");
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString().substring(1);
        System.out.println("客户端-" + remoteAddress + ":" + new String(msg.toString().getBytes(), Charset.forName("UTF-8")));
        //通知其他客户端
        channelMap.keySet().forEach(key -> {
            if(!key.equals(remoteAddress)) {
                channelMap.get(key).writeAndFlush(Unpooled.copiedBuffer("客户端-" +
                        channel.remoteAddress() + " :  " + msg.toString(), CharsetUtil.UTF_8));
            }
        });
        System.out.println("消息已转发至其他客户端");
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("执行client-exceptionCaught");
        cause.printStackTrace();
        //直接关闭管道的连接
        ctx.close();
    }

}
