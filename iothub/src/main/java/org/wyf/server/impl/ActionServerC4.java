package org.wyf.server.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.wyf.cache.CacheConst;
import org.wyf.cache.dto.DevicePlusStatusDTO;
import org.wyf.common.Const;
import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.dto.WeChatDataDTO;
import org.wyf.order.entity.OrderRecord;
import org.wyf.order.service.IOrderRecordService;
import org.wyf.server.BaseServer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wyf.cache.CacheConst;
import org.wyf.cache.dto.DevicePlusStatusDTO;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 上报设备状态
 * @author wangyifei
 */
@Component
@Slf4j
public class ActionServerC4 extends BaseServer {
    @Autowired
    private IOrderRecordService orderRecordService;
    @Override
    public String cmd() {
        return "C4";
    }

    @Override
    protected void handler0(Channel channel, DataDTO data, DataDTO2 dto2) {
            String key  = String.format(CacheConst.DEVICE_PLUS_STATUS,data.getDeviceNo(),dto2.getPlus() );
        // 00 空闲   ；  01 工作    ；  qita  故障
        DevicePlusStatusDTO devicePlusStatusDTO;
            if(redisUtil.exists(key)){
                 devicePlusStatusDTO = JSONUtil.toBean((String)redisUtil.get(key),DevicePlusStatusDTO.class) ;
            }else{
                 devicePlusStatusDTO = new DevicePlusStatusDTO();
            }




            if("00".equals(dto2.getData())){
                // 充电完成
                String orderNo = devicePlusStatusDTO.getOrderNo();
                // 清除插头和订单的绑定关系
                devicePlusStatusDTO.setStatus(0);
                devicePlusStatusDTO.setOrderNo("");
                // 修改最后一个订单的 结束时间
                if(StrUtil.isNotBlank(orderNo)){
                    OrderRecord     orderRecord = updOrderRecord(orderNo);
                    // 发送订阅消息
                    String json = sendSubscribe(orderRecord);
                    log.info("[{}] 订阅消息执行结果[{}]",channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(),json);
                }



            }else if("01".equals(dto2.getData())){
                //占用
                if(redisUtil.exists(String.format(CacheConst.ORDER_KEY,data.getDeviceNo()))){
                    // 符合的，说明是刷卡的；需要设置插头号 充电开始时间 充电状态
                    OrderRecord orderRecord = JSONUtil.toBean((String)redisUtil.get(String.format(CacheConst.ORDER_KEY,data.getDeviceNo())),OrderRecord.class);
                    devicePlusStatusDTO.setOrderNo(orderRecord.getOrderNo());
                    redisUtil.remove(String.format(CacheConst.ORDER_KEY,data.getDeviceNo()));
                    // 设置充电插头
                    orderRecord.setPlugNo(dto2.getPlus());
                    orderRecord.setChargingStartTime(new Date());
                    orderRecord.setChargingStatus(1);
                    orderRecordService.updateById(orderRecord);
                }
                // 修改缓存中的状态
                devicePlusStatusDTO.setStatus(1);
            }else{
                // 清除插头和订单的绑定关系
                devicePlusStatusDTO.setStatus(2);
                devicePlusStatusDTO.setOrderNo("");
            }
        redisUtil.set(key,JSONUtil.toJsonStr(devicePlusStatusDTO));
        channel.writeAndFlush(BaseServer.packageResult(data,dto2,"01"));


    }

    /**
     * 修改订单状态
     * @param orderNo
     * @return
     */
    private  OrderRecord updOrderRecord(String orderNo){
        QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no",orderNo);
        OrderRecord orderRecord =orderRecordService.getOne(queryWrapper);
        if(orderRecord!=null){
            orderRecord.setChargingStatus(2);
            orderRecord.setChargingEndTime(new Date());
            orderRecordService.update(orderRecord,queryWrapper);
        }

        return orderRecord;
    }

    /**
     *  发送订阅消息
     * @param orderRecord
     * @return
     */
    private String sendSubscribe(OrderRecord orderRecord){
        String openid = (String) redisUtil.get(String.format(CacheConst.USER_OPENID_KEY,orderRecord.getPhone()));
        Map<String,Object> map = new HashMap<>();
        map.put("touser",openid);
        map.put("template_id","vB0EmJKXE9uiCVz_Vr8VArIu6zw-Iwdi0nA9dQ-mS5Q");
        map.put("page","pages/main/main");
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("thing2",new WeChatDataDTO("充电完成"));
        dataMap.put("date10",new WeChatDataDTO(DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")));
        dataMap.put("character_string11",new WeChatDataDTO(orderRecord.getOrderNo()));
        dataMap.put("amount9",new WeChatDataDTO("￥"+orderRecord.getAmount().toString()));
        map.put("data",dataMap);
        String json =  HttpUtil.post("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token="+ Const.accesstoken,JSONUtil.toJsonStr(map));
        return json;
    }

}
