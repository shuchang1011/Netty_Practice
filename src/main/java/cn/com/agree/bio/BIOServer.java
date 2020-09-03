package cn.com.agree.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
/**
 * BIOServer服务器构建
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/25 23:02
 */

public class BIOServer {

    public static void main(String[] args) {

        //1.创建线程池
        ExecutorService executor = Executors.newCachedThreadPool();
        //2.通过ServerSocket监听客户端连接，进行io操作
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(7777);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //3.轮询监听客户端连接，并执行io操作
        while (true) {
            try {
                //等待客户端连接
                final Socket socket = serverSocket.accept();
                if(socket.isConnected()) {
                    executor.execute(new Runnable() {
                        public void run() {
                            try {
                                //通过字符流输出连接信息至客户端
                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "GB2312"));
                                bw.write("已连接socketServer");
                                bw.flush();

                                //获取输入流，并转化成字符串输出
                                InputStream inputStream = socket.getInputStream();
                                byte[] buff = new byte[1024];
                                int len = 0;
                                while((len = inputStream.read(buff)) != -1) {
                                    System.out.println(Thread.currentThread().getName() + " : " + new String(buff, 0, len));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    socket.close();  //关闭socket
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
