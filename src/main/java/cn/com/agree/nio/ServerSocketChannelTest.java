package cn.com.agree.nio;
/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * 测试NIO构建服务端，以及多个缓冲区读写数据
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/26 10:03
 */

public class ServerSocketChannelTest {

    public static void main(String[] args) {

        try {
            //1.创建服务端和客户端通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(7777);
            serverSocketChannel.socket().bind(inetSocketAddress);

            final SocketChannel socketChannel = serverSocketChannel.accept();  //建立连接

            //创建buffer数组
            ByteBuffer[] buffers = new ByteBuffer[2];
            buffers[0] = ByteBuffer.allocate(5);
            buffers[1] = ByteBuffer.allocate(5);
            int maxLength = 10;

            socketChannel.read(buffers);

            //flip方法是将limit限制改为当前写入的数据长度，然后将position置0,从缓冲区读的时候就从0读到数据长度
            Arrays.asList(buffers).forEach(buffer -> buffer.flip()); //转化buffer流向
            Arrays.asList(buffers).forEach(buffer -> System.out.println(new String(buffer.array())));
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
