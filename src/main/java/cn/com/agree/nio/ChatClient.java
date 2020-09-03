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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 聊天客户端构建
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/26 13:37
 */

public class ChatClient {

    private Selector selector;

    private SocketChannel socketChannel;

    public void connectServer() {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 7777);
            socketChannel.connect(inetSocketAddress);
            if(!socketChannel.isConnected()) {
                System.out.println("客户端" + socketChannel.getLocalAddress() + "正在连接中，请耐心等待");
                while (!socketChannel.finishConnect()) {
                    continue;
                }
            }
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readData() {
        int count = 0;
        try {
            count = selector.select();
            if(count > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if(key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel)key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        socketChannel.read(byteBuffer);
                        System.out.println(new String(byteBuffer.array()));
                    }
                    iterator.remove();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        executor.execute(new Runnable() {
            public void run() {
                chatClient.connectServer();
                while (true) {
                    chatClient.readData();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            chatClient.sendMessage(s);
        }

    }
}
