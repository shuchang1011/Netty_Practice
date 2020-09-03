/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
package cn.com.agree.netty.tcp;

/**
 * 自定义消息传送协议
 * 通过byte数组来承载传输消息，length来确保一次传输的长度，避免tcp粘包和拆包问题
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/30 23:25
 */

public class MyMessageProtocal {

    private int length;  //传输长度

    private byte[] content;   //传输文本内容

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
