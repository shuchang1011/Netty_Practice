/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
package cn.com.agree.netty.afa.natp.codec;

import cn.com.agree.Constants;
import cn.com.agree.netty.afa.natp.protocol.NATPProtocol;
import cn.com.agree.netty.afa.natp.utils.CRCUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 自定义NATP协议解码器
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/30 23:28
 */

public class NATPDecoder extends ByteToMessageDecoder {

    //head(4) + length(4) + reserved(6) + version(1) + transCode(20) + templateCode(20) + reservedCode(20) + crc(4)
    private final int BASED_LENGTH = 79;   //数据包长度至少为79

    private byte[] data = new byte[128];   //定义一个承载数据包的byte数组

    private int count = 0;

    /**
     * 获取byteBuf中的byte数组，通过ObjectInputStream
     * @param channelHandlerContext
     * @param byteBuf
     * @param list
     * return void
     */
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //将数据拷贝到本地byte[]
        toBuffer(byteBuf);

        //数据未达到基础长度
        if(count < BASED_LENGTH) {
            return;
        }
        int length = getIntFromBytes(data, 0);
        //数据包未完整接收
        if(count < 4 + length + 4) {
            return;
        }
        byte[] pack = new byte[length + 4];
        System.arraycopy(data, 4, pack, 0, length + 4);
        //校验tcp传输过程中，是否出现丢包状况
        int crc = getIntFromBytes(pack, pack.length - 4);
        if(!CRCUtil.getInstance().AFA_NATPCheckCRC32(crc, pack, length)) {
            System.out.println("CRC校验失败，数据包已损坏");
        } else {
            //校验和有效
            //通过byteArrayInputStream将字节数组转化成为字节输入流，然后作为参数通过对象流读取相应的对象
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(pack));
            NATPProtocol natpProtocol = (NATPProtocol) objectInputStream.readObject();
            natpProtocol.setSrcCRC32(crc);
            list.add(natpProtocol);
            objectInputStream.close();  //使用完记得释放
        }
        //清除已读取的数据
        skip(4 + length + 4);


        /*//标记当前下标
        byteBuf.markReaderIndex();
        int length = byteBuf.readInt();

        if(length <= byteBuf.readableBytes()) {
            byte[] pack = new byte[length];
            byteBuf.readBytes(pack);

            //校验tcp传输过程中，是否出现丢包状况
            int crc = byteBuf.readInt();
            if(!CRCUtil.getInstance().AFA_NATPCheckCRC32(crc, pack, pack.length)) {
                System.out.println("CRC校验失败，数据包已损坏");
            } else {
                //校验和有效
                //通过byteArrayInputStream将字节数组转化成为字节输入流，然后作为参数通过对象流读取相应的对象
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(pack));
                NATPProtocol natpProtocol = (NATPProtocol) objectInputStream.readObject();
                natpProtocol.setSrcCRC32(crc);
                list.add(natpProtocol);
                objectInputStream.close();  //使用完记得释放
            }
        } else {
            byteBuf.resetReaderIndex();
        }*/
    }

    //将byteBuf中的数据写入到自定义的byte[]中
    public void toBuffer(ByteBuf buffer) {
        ensureCapacity(buffer.readableBytes() + count);
        int base = count;
        count += buffer.readableBytes();
        buffer.readBytes(data, base, buffer.readableBytes());
    }

    public void ensureCapacity(int capacity) {
        if(capacity - data.length > 0) {
            grow(capacity);
        }
    }

    public void grow(int capacity) {
        int oldCapacity = data.length;
        //扩容一倍
        int newCapacity = oldCapacity << 1;
        if(newCapacity - capacity < 0) {
            newCapacity = capacity;
        }
        if (newCapacity < 0) {
            if (capacity < 0) // overflow
                throw new OutOfMemoryError();
            newCapacity = Integer.MAX_VALUE;
        }
        data = Arrays.copyOf(data, newCapacity);
    }

    public void skip(int index) {
        count -= index;
        System.arraycopy(data, index, data, 0, count);
    }

    //将四位byte转化成int
    public static int getIntFromBytes(byte[] data, int startIndex) {
        return (data[startIndex] & 0xff) << 24 | (data[startIndex + 1] & 0xff) << 16 | (data[startIndex + 2] & 0xff) << 8 | data[startIndex + 3] & 0xff;
    }

}
