/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
package cn.com.agree.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 自定义消息解码器
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/30 23:28
 */

public class MyMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        //获取文本长度
        int length = byteBuf.readInt();

        //声明一个长度为length的byte数组，并读取响应长度的数据
        byte[] content = new byte[length];
        byteBuf.readBytes(content);

        //解析完毕后，重新封装进自定义的协议中
        MyMessageProtocal messageProtocal = new MyMessageProtocal();
        messageProtocal.setLength(length);
        messageProtocal.setContent(content);

        //将封装好的协议添加到list中，传递到下一个handler进行处理
        list.add(messageProtocal);

    }
}
