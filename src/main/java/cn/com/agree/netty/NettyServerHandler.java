package cn.com.agree.netty;
/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
/**
 * 定义服务端管道的处理器类
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/27 10:24
 */

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //客户端已连接，通道就绪
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("执行client-channelActive");
        Channel channel = ctx.channel();
        System.out.println("客户端：" + channel.remoteAddress() + "已上线");
    }

    //通道关闭，断开连接
    public void channelInActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("执行client-channelInActive");
        Channel channel = ctx.channel();
        System.out.println("客户端：" + channel.remoteAddress() + "离线");
    }

    //监听read事件的发生
    //ChannelHandlerContext负责处理pipeline和ChannelHandler的交互
    //每次往pipeline中插入一个处理器，都会生成一个对应的ChannelHandlerContext对象
    //ChannelHandler可以通过与之关联的上下文对象将事件传递给pipeline中的下一个处理器对象
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("执行client-channelRead");
        Channel channel = ctx.channel();
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端-" + channel.remoteAddress().toString().substring(1) + "发送消息：" + byteBuf.toString(CharsetUtil.UTF_8));


        //当我们想在handler监听的某个事件中处理一个耗时较长的任务时
        //我们可以把它放到NioEventLoop的taskQueue任务队列中，让它能够异步执行
        //而我们的Reactor工作组线程池可以继续监听事件的发生，而taskQueue队列则通过一个单线程来执行任务队列中的等待的任务
        channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    //通过channel的writeAndFlush方法写入数据直接从pipeline末端流出，不会流经管道中的handler
                    channel.writeAndFlush(Unpooled.copiedBuffer("hello,我已收到发送的消息", CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        //工作队列只有一个线程执行队列中的任务，因此此任务可能会造成阻塞
        channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    channel.writeAndFlush(Unpooled.copiedBuffer("再次确认收到消息", CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //read事件完成后的操作
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("执行client-channelReadComplete");
        //响应客户端的消息发送操作
        //通过ChannelHandlerContext进行write写操作，会使我们写入的数据流向pipeline中的下一个处理器
        //而通过Channel执行write操作的话，写的数据会直接从pipeline的末端流向我们的客户端
        ctx.writeAndFlush(ctx.alloc().directBuffer().writeBytes(("消息已送达").getBytes()));
    }

    //异常捕获
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("执行client-exceptionCaught");
        //直接关闭管道的连接
        ctx.close();
    }
}
