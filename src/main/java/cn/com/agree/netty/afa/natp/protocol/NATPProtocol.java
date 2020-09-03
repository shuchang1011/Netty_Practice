/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 *
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
package cn.com.agree.netty.afa.natp.protocol;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * NATP协议
 *
 * @author shuchang
 * @version 1.0
 * @date 2020/8/31 13:39
 */


@Data
public class NATPProtocol implements Serializable {

    /*Packlen	UINT	4	全包长度（包头加 文件数据）writeInt 不包含自身及crc校验数据
    Reserved	CHAR	6
    Version	CHAR	1	0x10(2.0报文)/0x30(3.0报文)
    TransCode	CHAR	20	交易代码

    TemplateCode	CHAR	20	应用代码

    ReservedCode	CHAR	20	保留域*/

    private int dataLength;

    private String reserved;

    private short version;

    private String transCode;

    private String templateCode;

    private String reservedCode;

    //数据实体通过map进行封装
    private Map<String, Object> content = new HashMap<String, Object>();

    //校验和
    private int SrcCRC32;

}
