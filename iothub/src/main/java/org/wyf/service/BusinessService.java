package org.wyf.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.wyf.order.entity.OrderRecord;
import org.wyf.order.service.IOrderRecordService;
import org.wyf.system.model.User;
import org.wyf.system.service.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.wyf.system.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Component
public class BusinessService {
    @Autowired
    private IUserService userService;
    @Autowired
    private IOrderRecordService orderRecordService;

    @Transactional(rollbackFor = Exception.class)
    public OrderRecord comsume(String phone,String consumHex,String deviceNo,String plug,String cardNo)throws Exception{

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",phone).eq("is_del",0);
        User user  = userService.getOne(queryWrapper);

        // 积分 转 钱 100:1
        BigDecimal consum = new BigDecimal(Integer.parseInt(consumHex,16)).divide(new BigDecimal("100"),RoundingMode.HALF_UP);
        user.setBalance(user.getBalance().subtract(consum));
        userService.updateById(user);

        Date now = new Date();

        // 生成一个消费记录
        OrderRecord orderRecord = new OrderRecord();

        orderRecord.setStatus(1);
        orderRecord.setAmount(consum);
        orderRecord.setCmdStatus(2);
        orderRecord.setOrderTag(2);
        orderRecord.setDeviceNo(deviceNo);
        orderRecord.setPhone(phone);
        orderRecord.setCardNo(cardNo);
        orderRecord.setPlugNo(null);
        orderRecord.setChargingStatus(0);
        // 订单号 = 设备号+当前时间戳
        orderRecord.setOrderNo(deviceNo+System.currentTimeMillis());
        // 支付时间，指令发送时间，指令回复时间 都是当前时间
        orderRecord.setPayTime(now);
        orderRecord.setCmdSendTime(now);
        orderRecord.setCmdReplyTime(now);
        orderRecord.setIsDel(0);
        orderRecord.setCreateTime(now);
        orderRecordService.save(orderRecord);




        return orderRecord;
    }



}
