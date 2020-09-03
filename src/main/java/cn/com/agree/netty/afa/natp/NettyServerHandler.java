package cn.com.agree.netty.afa.natp;/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */

import cn.com.agree.netty.afa.natp.protocol.NATPProtocol;
import cn.com.agree.netty.tcp.MyMessageProtocal;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * 职责描述
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/30 23:43
 */

public class NettyServerHandler extends SimpleChannelInboundHandler<NATPProtocol> {

    private static Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NATPProtocol natpProtocol) throws Exception {
        System.out.println("事件channelRead");
        Channel channel = channelHandlerContext.channel();
        String remoteAddress = channel.remoteAddress().toString().substring(1);
        logger.info("客户端" + remoteAddress + "发来数据包\n" +
                "[length]:" + natpProtocol.getDataLength() +
                "[Reserved]:" + natpProtocol.getReserved() +
                "[version]:" + natpProtocol.getVersion() +
                "[transCode]:" + natpProtocol.getTransCode() +
                "[templateCode]:" + natpProtocol.getTemplateCode() +
                "[reservedCode]:" + natpProtocol.getReservedCode());
        Map<String, Object> content = natpProtocol.getContent();
        for (String key : content.keySet()) {
            System.out.println(key + " : " + content.get(key));
        }
    }

    //客户端已建立连接，通道处于就绪状态
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("事件channelActive");
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString().substring(1);
        System.out.println("客户端-" + remoteAddress + "已上线");
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("事件channelInactive");
        Channel channel = ctx.channel();
        String remoteAddress = channel.remoteAddress().toString().substring(1);
        System.out.println("客户端-" + remoteAddress + "已离线");
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("执行client-exceptionCaught");
        cause.printStackTrace();
        //直接关闭管道的连接
        ctx.close();
    }

}
