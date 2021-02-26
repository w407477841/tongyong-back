package org.wyf.dto;

import cn.hutool.core.util.StrUtil;
import org.wyf.common.Const;
import org.wyf.common.ProtocolConst;
import lombok.Data;
import org.wyf.common.ProtocolConst;

import java.util.Map;

@Data
public class DataDTO {

    private String cmd;

    private String deviceNo;

    private String data;
    /** 数据体长度 */
    private Integer length;


    public DataDTO(String cmd, String deviceNo, String data, Integer length) {
        this.cmd = cmd;
        this.deviceNo = deviceNo;
        this.data = data;
        this.length = length;
    }


    public  String toData(){

         String hexLength = StrUtil.fillBefore(Integer.toHexString(data.length()/2+2),'0',4).toUpperCase();

         String result =  ProtocolConst.PROTOCOL_VERSION+this.deviceNo+this.cmd+hexLength+this.data;

         String crc = Const.getXorCRC(result);

         return ProtocolConst.PROTOCOL_HEADER+result+crc+ProtocolConst.PROTOCOL_FOOTER;


    }

}
