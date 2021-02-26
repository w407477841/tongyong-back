package org.wyf.server.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.wyf.cache.CacheConst;
import org.wyf.common.Const;
import org.wyf.dto.DataDTO;
import org.wyf.dto.DataDTO2;
import org.wyf.order.entity.OrderRecord;
import org.wyf.server.BaseServer;
import org.wyf.service.BusinessService;
import org.wyf.system.model.User;
import org.wyf.system.service.service.IUserService;
import io.netty.channel.Channel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wyf.cache.CacheConst;
import org.wyf.service.BusinessService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;


/**
 *  虚拟卡上报预消费信息
 * @author wangyifei
 */
@Slf4j
@Component
public class ActionServerA8 extends BaseServer {
    @Autowired
    private BusinessService businessService;

    @Override
    public String cmd() {
        return "A8";
    }

    @Override
    protected void handler0(Channel channel, DataDTO data,DataDTO2 dto2) {
        String cardNoHex = dto2.getData().substring(24,32);
        String orderSnHex = dto2.getData().substring(32,36);
        String consumHex = dto2.getData().substring(36,42);

        // 修改 账户余额
        String phoneKey = String.format(CacheConst.CARD_PHONE_KEY,cardNoHex);
        if(!redisUtil.exists(phoneKey)){
            channel.writeAndFlush(BaseServer.packageResult(data,dto2,"02"+orderSnHex+"000000"));
            log.info("[{}] 卡[{}]还未绑定用户",channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(),cardNoHex);
            return;
        }
        String phone  = (String) redisUtil.get(phoneKey);


        String key = String.format(CacheConst.ORDER_LOCK_KEY,phone);
        String lastA8Key = String.format(CacheConst.LAST_A8_KEY,data.getDeviceNo());
        String lastA8 = (String) redisUtil.get(lastA8Key);
        if(dto2.getData().equals(lastA8)){
            // 重发的
            log.info("[{}] 指令重发[{}]",channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(), phone);
            channel.writeAndFlush(BaseServer.packageResult(data,dto2,"01"+orderSnHex+"000000"));
            return ;
        }
        RLock lock =  redissonClient.getLock(key);
        boolean res =  redisUtil.lock(lock,key);
        if(res){
                //修改用户的钱
            try {
             OrderRecord orderRecord = businessService.comsume(phone,consumHex,data.getDeviceNo(),dto2.getPlus(),cardNoHex);
             redisUtil.set(String.format(CacheConst.ORDER_KEY,data.getDeviceNo()),JSONUtil.toJsonStr(orderRecord));
             redisUtil.set(lastA8Key,dto2.getData());
                // 成功
             channel.writeAndFlush(BaseServer.packageResult(data,dto2,"01"+orderSnHex+"000000"));

            } catch (Exception e) {
                e.printStackTrace();
                channel.writeAndFlush(BaseServer.packageResult(data,dto2,"00"+orderSnHex+"000000"));
            }finally {
                if(res){
                    lock.unlock();
                }

            }

        }else{
            // 没拿到锁
            log.info("[{}] 没拿到锁[{}],扣款失败",channel.attr(Const.NETTY_CHANNEL_UUID_KEY).get(), phone);
            channel.writeAndFlush(BaseServer.packageResult(data,dto2,"00"+orderSnHex+"000000"));
        }

    }
}
