package org.wyf.dto;

import cn.hutool.core.util.StrUtil;
import org.wyf.common.Const;
import org.wyf.common.ProtocolConst;
import lombok.Data;

@Data
public class DataDTO2 {
    private String cmd;

    private String plus;

    private String data;
    /** 数据体长度 */
    private Integer length;

    public DataDTO2(String cmd, String plus, String data, Integer length) {
        this.cmd = cmd;
        this.plus = plus;
        this.data = data;
        this.length = length;
    }

    public  String toData(){

        String hexLength = StrUtil.fillBefore(Integer.toHexString(data.length()/2),'0',2).toUpperCase();

        String result =  "AA"+this.plus+"00"+this.cmd+hexLength+this.data;

        String crc = Const.getXorCRC(result);

        return result+crc;


    }

    public static void main(String[] args) {

        System.out.println(  StrUtil.fillBefore(Integer.toHexString(16),'0',2));

    }
}
