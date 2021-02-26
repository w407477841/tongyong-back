package org.wyf;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import org.wyf.cache.CacheConst;
import org.wyf.cache.RedisUtil;
import org.wyf.common.Const;
import org.wyf.dto.WeChatDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wyf.cache.CacheConst;
import org.wyf.cache.RedisUtil;
import org.wyf.common.Const;
import org.wyf.dto.WeChatDataDTO;

import java.io.Console;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
@Slf4j
public class OnlineTimer {
    @Autowired
    private RedisUtil redisUtil ;
    @Value("${server.port}")
    private Integer port;

    @Scheduled(cron = "0/20 * * * * ?")
    public  void online(){
        log.info("平台自检心跳");
        redisUtil.set(CacheConst.NETTY_ONLINE_KEY,"http://"+SystemUtil.getHostInfo().getAddress()+":"+port,30L,TimeUnit.SECONDS);
    }
    @Scheduled(initialDelay = 1,fixedRate = 2*60*60*1000L)
    public void accesstoken(){
        Map<String,Object> map = new HashMap<>();
        map.put("grant_type","client_credential");
        map.put("appid","wx455be876ba4ad42e");
        map.put("secret","be6d3ea1f4d749e0f96ee64e5a8f6fbf");
        String json =  HttpUtil.get("https://api.weixin.qq.com/cgi-bin/token",map);
        log.info("accesstoken[{}]",json);
        Map<String,Object> result = JSONUtil.toBean(json,Map.class);

        Const.accesstoken = (String)result.get("access_token");
        redisUtil.set(CacheConst.ACCESS_TOKEN_KEY,Const.accesstoken);

    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("touser","o4-fN4m0KYEO1PnhuBHwewdyZKPU");
        map.put("template_id","nNiHD4G30g4eDhaUfMzBBhf7GNTcmDypzKkPoQXNvqc");
        map.put("page","index");
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("thing1",new WeChatDataDTO("asdad123"));
        dataMap.put("character_string2",new WeChatDataDTO("asdad123"));
        map.put("data",dataMap);
        log.info("订阅消息  [{}]",JSONUtil.toJsonStr(map));

        String json =  HttpUtil.post("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=28_dDmL0nJKFQ9OEpyf7FZvItSGxqrT6B9hcb2VH_LGskd8mnstFmo1KZLHkrdcLBiafKgMk-gRIaB1MoXEpIjvyqxs9hTsWVf3Tjvrhp-3cBfscdKNWpbPxmHpS5Q0Rl_WCDHL-c4M9wpAy1ziWFEbAHAPYX",JSONUtil.toJsonStr(map));
        System.out.println(json);
    }


}
