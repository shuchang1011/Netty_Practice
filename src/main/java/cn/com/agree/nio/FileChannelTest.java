package cn.com.agree.nio;
/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * FileChanner使用测试
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/26 9:21
 */

public class FileChannelTest {

    public static void main(String[] args) {

        new FileChannelTest().copyFile();


    }

    public void writeToFile() {

        try {
            //1.创建一个输出流，并通过输出流获取channel
            FileOutputStream out = new FileOutputStream("D:\\fileChannelTest.txt");
            final FileChannel channel = out.getChannel();
            //2.通过byteBuffer读取字符串并写入到channel中
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put(("hello,world!").getBytes());
            buffer.flip(); //反转buffer的流向
            channel.write(buffer);
            channel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromFile() {

        try {
            //1.获取输入流，并转化成channel
            File file = new File("D:\\fileChannelTest.txt");
            FileInputStream inputStream = new FileInputStream(file);
            final FileChannel channel = inputStream.getChannel();

            //2.从通道中读取数据到buffer,并输出到控制台
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while(true) {  //循环读取直到全部读取到buffer中
                buffer.clear(); //清空缓存区，只是把标记初始化，数据不会清楚
                int read = channel.read(buffer);
                if (read == -1) {  //读取完毕，退出循环
                    break;
                }
            }
            System.out.println("content is " + new String(buffer.array()));
            channel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void copyFile() {

        try {
            //1.创建输入输出流，并转化成相应的通道
            FileInputStream inputStream = new FileInputStream(new File("D:\\fileChannelTest.txt"));
            FileOutputStream outputStream = new FileOutputStream(new File("D:\\transformFile.txt"));
            FileChannel sourceChannel = inputStream.getChannel();
            FileChannel toChannel = outputStream.getChannel();

            //2.文件传输
            toChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

            sourceChannel.close();
            toChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
