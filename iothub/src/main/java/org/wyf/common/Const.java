package org.wyf.common;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import	java.util.concurrent.atomic.LongAdder;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author : wangyifei
 * Description
 * Date: Created in 10:32 2019/3/25
 * Modified By : wangyifei
 */
@Slf4j
public class Const {
    /** 当前连接数*/
   public static   LongAdder CURRENT_LINKS = new LongAdder();

   public static String accesstoken = "";
    /**
     * 管理所有的连接
     */
    public static   Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    public static AttributeKey<DeviceSerssion> NETTY_CHANNEL_KEY = AttributeKey.valueOf("netty.channel");


    public static AttributeKey<String> NETTY_CHANNEL_UUID_KEY = AttributeKey.valueOf("netty.channel.uuid");


    /**
     *  byteBuf 转 字符窜
     * @param in
     * @return
     */
    public static String bytebuf2ascii(ByteBuf in){
        int len = in.readableBytes();
        //  可读开始位置
        byte[] msgData = new byte[len];
        in.readBytes(msgData);
        String data = new String(msgData);
        return data;
    }

    /**
     * byte转 16进制 字符窜
     * @param in
     * @return
     */
    public static  String bytebuf2hex(ByteBuf in){
        int len = in.readableBytes();
        //  可读开始位置
        byte[] msgData = new byte[len];
        in.readBytes(msgData);
        String data = toHexString(msgData);
        return data;
    }

    /**
     * byte数组转16进制字符串
     * @param bytes
     * @return
     * @description
     */
    public   static String toHexString(byte[] bytes) {
        final String hex = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(hex.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(hex.charAt(b & 0x0f));
        }
        return sb.toString().toUpperCase();
    }

    /**
     *  异或crc
     * @param resultData
     * @return
     */
    public  static String getXorCRC(String resultData){
        byte[] datas =  HexUtil.decodeHex(resultData);
        byte crc = 0x00 ;
        for (int i = 0; i < datas.length; i++) {
            crc = (byte)(crc ^ datas[i]);
        }
        return StrUtil.fillBefore(Integer.toHexString(crc & 0xFF),'0',2).toUpperCase();
    }


    /**
     * 根据16进账字符串计算出CRC 校验码  高位在前  低位在后
     * @param crcOriginalString
     * @return
     */
    public   static String getCRC16(String crcOriginalString){
        String newStr=crcOriginalString.replaceAll(" ","");
        String regex = "(.{2})";
        newStr = newStr.replaceAll (regex, "$1 ");

        //计算出新的CRC16
        byte[] dd = Crc16Util.getData(newStr.split(" "));
        String crcString = Crc16Util.byteTo16String(dd).toUpperCase();
        //计算出来的校验位(去除多余的原始数据位,只保留CRC位)
        String crcCode = crcString.substring(crcString.length() - 6).replaceAll(" ", "");
        //将低位在前转为高位在前
        return  crcCode.substring(2, 4) + crcCode.substring(0, 2);
    }


    /**
     * 转译
     * 7D5D->7D
     * 7D5F->7F
     * 7D5E->7E
     * @param oriData
     * @return
     */
    public   static String translation (byte[] oriData){

        String oriStr = Const.toHexString(oriData);
        int index = oriStr.indexOf("7D5D");
        while (index>=0){
            if(index%2==0){
                oriStr =   oriStr.substring(0,index)+"7D"+oriStr.substring(index+4);
            }
            index = oriStr.indexOf("7D5D",index);
        }

        index = oriStr.indexOf("7D5F");
        while (index>=0){
            if(index%2==0){
                oriStr =   oriStr.substring(0,index)+"7F"+oriStr.substring(index+4);
            }
            index = oriStr.indexOf("7D5F",index);
        }

        index = oriStr.indexOf("7D5E");
        while (index>=0){
            if(index%2==0){
                oriStr =   oriStr.substring(0,index)+"7E"+oriStr.substring(index+4);
            }
            index = oriStr.indexOf("7D5E",index);
        }

        return oriStr;
    }

    /**
     * 转译
     * 7D->7D5D
     * 7F->7D5F
     * 7E->7D5E
     * @param oriData
     * @return
     */
    public   static byte[] reTranslation (String oriData){
        String header = oriData.substring(0,2);
        String footer = oriData.substring(oriData.length()-2);
        oriData = oriData.substring(2,oriData.length()-2);
        StringBuffer sb = new StringBuffer();
        sb.append(header);
        for(int i=0;i<oriData.length()/2;i++){
            String b = oriData.substring(i*2,i*2+2);
            if("7D".equalsIgnoreCase(b)){
                sb.append("7D").append("5D");
            }else if("7F".equalsIgnoreCase(b)){
                sb.append("7D").append("5F");
            }else if("7E".equalsIgnoreCase(b)){
                sb.append("7D").append("5E");
            }else{
                sb.append(b);
            }

        }
        sb.append(footer);
        log.info("发送数据[{}]",sb.toString());
        return HexUtil.decodeHex(sb.toString());
    }


    public static void main(String[] args) {
        String crc  ="9A";
        System.out.println(Const.getXorCRC("55014441AC5300AB0009AA0100B601011D"));

    }

}
