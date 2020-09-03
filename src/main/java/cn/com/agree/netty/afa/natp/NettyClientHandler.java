/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
package cn.com.agree.netty.afa.natp;

import cn.com.agree.netty.afa.natp.protocol.NATPProtocol;
import cn.com.agree.netty.tcp.MyMessageProtocal;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端处理器
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/30 23:48
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<MyMessageProtocal> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MyMessageProtocal myMessageProtocal) throws Exception {
        System.out.println(new String(myMessageProtocal.getContent(), Charset.forName("UTF-8")));
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString();
        System.out.println("已连接上服务端" + remoteAddress);
        //通道准备就绪后，发送十条消息，测试tcp粘包问题是否解决
        short version = 0x0001;
        for (int i = 0; i < 100; i++) {
            NATPProtocol natpProtocol = new NATPProtocol();
            natpProtocol.setDataLength(0);
            natpProtocol.setReserved(new String("123456"));
            natpProtocol.setVersion(version);
            natpProtocol.setTransCode(String.format("%20s", "transCode"));
            natpProtocol.setTemplateCode(String.format("%20s", "templateCode"));
            natpProtocol.setReservedCode(String.format("%20s", "reservedCode"));
            Map<String, Object> map = new HashMap<String, Object>();
            for (int j = 0; j < 10; j++) {
                map.put("key1" + i + j, "value" + i + j);
                map.put("key2" + i + j, "value" + i + j);
                map.put("key3" + i + j, "value" + i + j);
                map.put("key4" + i + j, "value" + i + j);
            }
            natpProtocol.setContent(map);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(natpProtocol);
            natpProtocol.setDataLength(byteArrayOutputStream.size());
            ctx.writeAndFlush(natpProtocol);
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
