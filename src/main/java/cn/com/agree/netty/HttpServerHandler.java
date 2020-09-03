package cn.com.agree.netty;
/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * http服务监听处理器
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/28 13:45
 */

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        //httpObject是否是浏览器的请求
        if(httpObject instanceof HttpRequest) {
            System.out.println("客户端" + channelHandlerContext.channel().remoteAddress() + "发来了请求");
            //构造响应报文信息
            ByteBuf byteBuf = Unpooled.copiedBuffer("hello,客户端" + channelHandlerContext.channel().remoteAddress() +
                    ",我已收到请求", CharsetUtil.UTF_8);
            DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            //设置请求头，响应类型为json格式，响应信息长度为返回数据的长度
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.array().length);

            channelHandlerContext.writeAndFlush(httpResponse);

        }
    }
}
