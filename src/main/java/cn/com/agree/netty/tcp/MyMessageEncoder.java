package cn.com.agree.netty.tcp;/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义编码器，将按照自定义的协议封装好的消息转化成二进制进行网络传输
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/30 23:37
 */

public class MyMessageEncoder extends MessageToByteEncoder<MyMessageProtocal> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MyMessageProtocal myMessageProtocal, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(myMessageProtocal.getLength());
        byteBuf.writeBytes(myMessageProtocal.getContent());
    }
}
