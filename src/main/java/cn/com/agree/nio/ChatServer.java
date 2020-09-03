package cn.com.agree.nio;/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 聊天服务器
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/26 12:00
 */

public class ChatServer {

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private static final int PORT = 7777;

    /**
    * 初始化操作
    */
    public ChatServer() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(PORT);
            serverSocketChannel.socket().bind(inetSocketAddress);  //绑定通讯端口
            serverSocketChannel.configureBlocking(false); //设置为非阻塞
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);  //注册accept事件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        try {
            while(true) {//轮询监听事件
                int count = selector.select();
                if(count > 0) {  //监听到了事件发生
                    //1.输出上线客户端，并通知其他在线客户端
                    Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                    while(iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if(key.isAcceptable()) {  //连接事件处理
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            String message = "client:" + socketChannel.getRemoteAddress() + "上线了";
                            System.out.println(message);
                            socketChannel.register(selector, SelectionKey.OP_READ);  //注册read事件
                            notifyOthers(message, socketChannel); //发送上线通知给其他客户端
                        } else if (key.isReadable()) { //读取数据处理
                            try {
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                SocketChannel socketChannel = (SocketChannel)key.channel();
                                socketChannel.read(buffer);
                                String message = new String(buffer.array());
                                System.out.println("client " + socketChannel.getRemoteAddress().toString().substring(1) +
                                        " : " + message);
                                notifyOthers(message, socketChannel); //通知其他客户端
                            } catch (IOException e) {
                                SocketChannel socketChannel = (SocketChannel)key.channel();
                                System.out.println("client:" + socketChannel.getRemoteAddress().toString().substring(1) +
                                        "离线了");
                                key.cancel();
                                socketChannel.close();
                            }
                        }
                        iterator.remove();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void notifyOthers(String message, SocketChannel socketChannel) {
        //遍历key集合，排除当前的socketChannel
        for (SelectionKey key : selector.keys()) {
            Channel channel = key.channel();
            //注册到key中的channel有两种可能：ServerSocketChannel和SocketChannel
            //所以这里需要先判断类型，再进行比较
            if(channel instanceof SocketChannel && channel != socketChannel) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
                SocketChannel dest = (SocketChannel)channel;
                //将buffer 的数据写入通道
                try {
                    dest.write(byteBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {

        new ChatServer().listen();

    }
}
