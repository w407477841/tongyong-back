package org.wyf.server.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.wyf.cache.CacheConst;
import org.wyf.cache.dto.DevicePlusStatusDTO;
import org.wyf.common.Const;
import org.wyf.common.dto.ResultDTO;
import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.dto.DeviceDTO;
import org.wyf.order.entity.OrderRecord;
import org.wyf.order.service.IOrderRecordService;
import org.wyf.server.BaseServer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wyf.cache.CacheConst;
import org.wyf.cache.dto.DevicePlusStatusDTO;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 网络通道充电任务 反馈
 * @author wangyifei
 */
@Slf4j
@Component
public class ActionServerA7 extends BaseServer {
    @Autowired
    private IOrderRecordService orderRecordService;


    @Override
    public String cmd() {
        return "A7";
    }

    @Override
    protected void handler0(Channel channel, DataDTO data, DataDTO2 dto2) {
        String key  = String.format(CacheConst.DEVICE_PLUS_STATUS,data.getDeviceNo(),dto2.getPlus() );
        try {
            if("01".equals(dto2.getData())){
                // 成功
                if(redisUtil.exists(key)){
                    DevicePlusStatusDTO devicePlusStatusDTO = JSONUtil.toBean((String)redisUtil.get(key),DevicePlusStatusDTO.class) ;
                    log.info("[{}] 网络通道充电任务[{}][{}]打开成功",channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(),data.getDeviceNo(),dto2.getPlus());

                    OrderRecord record = new OrderRecord();
                    record.setCmdStatus(2);
                    record.setCmdReplyTime(new Date());
                    record.setChargingStatus(1);
                    record.setChargingStartTime(new Date());
                    QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("is_del",0);
                    queryWrapper.eq("order_no",devicePlusStatusDTO.getOrderNo());
                    orderRecordService.update(record,queryWrapper);
                    devicePlusStatusDTO.setStatus(1);
                    redisUtil.set(key,JSONUtil.toJsonStr(devicePlusStatusDTO));
                }else{
                    DevicePlusStatusDTO devicePlusStatusDTO = new DevicePlusStatusDTO();
                    devicePlusStatusDTO.setStatus(1);
                    redisUtil.set(key,JSONUtil.toJsonStr(devicePlusStatusDTO));
                    log.info("[{}] 网络通道充电任务[{}][{}]打开成功,未找到对应的订单",channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(),data.getDeviceNo(),dto2.getPlus());
                }

            }else if("00".equals(dto2.getData())){
                // 失败
                DevicePlusStatusDTO devicePlusStatusDTO = JSONUtil.toBean((String)redisUtil.get(key),DevicePlusStatusDTO.class) ;
                devicePlusStatusDTO.setStatus(2);
                redisUtil.set(key,JSONUtil.toJsonStr(devicePlusStatusDTO));
                log.info("[{}] 网络通道充电任务[{}][{}]插头故障",channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(),data.getDeviceNo(),dto2.getPlus());

                OrderRecord record = new OrderRecord();
                record.setCmdStatus(3);
                record.setCmdReplyTime(new Date());
                QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("is_del",0);
                queryWrapper.eq("order_no",devicePlusStatusDTO.getOrderNo());
                orderRecordService.update(record,queryWrapper);

            }
        }finally {
           // 此处解锁
            redisUtil.remove(String.format(CacheConst.MOBLIE_PAYMENT_STATUS_KEY, data.getDeviceNo(),dto2.getPlus()));
        }





    }

    @Override
    public void remoteProcess(DeviceDTO body) {
        String key  = String.format(CacheConst.DEVICE_PLUS_STATUS,body.getDeviceNo(),body.getPlug() );
        if(redisUtil.exists(key)){
            DevicePlusStatusDTO devicePlusStatusDTO = JSONUtil.toBean((String)redisUtil.get(key),DevicePlusStatusDTO.class) ;
            if(1==devicePlusStatusDTO.getStatus()){
                 throw  new RuntimeException("插头被占用") ;
            }else if(2==devicePlusStatusDTO.getStatus()){
                throw  new RuntimeException("插头故障");
            }
        }
    }

    @Override
    public void update(DeviceDTO body){

        OrderRecord record = new OrderRecord();
        record.setCmdStatus(1);
        record.setCmdSendTime(new Date());
        QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_del",0);
        queryWrapper.eq("order_no",body.getOrderNo());

        orderRecordService.update(record,queryWrapper);

    }



}
