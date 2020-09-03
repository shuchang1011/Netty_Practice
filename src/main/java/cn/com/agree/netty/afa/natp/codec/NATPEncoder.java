/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
package cn.com.agree.netty.afa.natp.codec;

import cn.com.agree.netty.afa.natp.protocol.NATPProtocol;
import cn.com.agree.netty.afa.natp.utils.CRCUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 自定义NATP协议编码器，将按照自定义的协议封装好的消息转化成二进制进行网络传输
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/30 23:37
 */

public class NATPEncoder extends MessageToByteEncoder<NATPProtocol> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NATPProtocol natpProtocol, ByteBuf byteBuf) {
        //NAPT协议封装顺序
        //包头--》 数据包--》 校验和
        //包头封装顺序 ： dataLength -> Reserved -> version -> transCode -> templateCode -> reservedCode
        //封装包头
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            //首先写入一个数据包的长度，方便解析时处理tcp粘包的问题
            byteBuf.writeInt(natpProtocol.getDataLength());
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(natpProtocol);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            byteBuf.writeBytes(bytes);
            //写入校验和
            byteBuf.writeInt(CRCUtil.getInstance().AFA_NATPClacCRC32(bytes, bytes.length));
            //最后使用\r\n，便于DelimiterBasedFrameDecoder对数据包进行截断
//            byteBuf.writeBytes(new byte[]{'\r','\n'});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //最后释放输出流
            try {
                byteArrayOutputStream.close();
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
