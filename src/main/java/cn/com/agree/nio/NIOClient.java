package cn.com.agree.nio;
/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * NIO客户端测试
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/26 11:13
 */

public class NIOClient {

    public static void main(String[] args) throws Exception{
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 5555);

        if(!socketChannel.connect(inetSocketAddress)) {
            while (!socketChannel.finishConnect()) {
                System.out.println("客户端正在连接中，请耐心等待");
            }
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap("hello,world".getBytes());
        socketChannel.write(byteBuffer);
        socketChannel.close();
    }

}
