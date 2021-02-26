package org.wyf.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.wyf.basic.entity.BasicPile;
import org.wyf.basic.entity.BasicStation;
import org.wyf.basic.service.IBasicPileService;
import org.wyf.basic.service.IBasicStationService;
import org.wyf.cache.CacheConst;
import org.wyf.cache.RedisUtil;
import org.wyf.cache.dto.DevicePlusStatusDTO;
import org.wyf.common.constant.Const;
import org.wyf.common.dto.ResultDTO;
import org.wyf.system.ConstSystem;
import org.wyf.dto.monitor.MonitorDTO;
import org.wyf.vo.monitor.MonitorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wyf.cache.CacheConst;
import org.wyf.cache.RedisUtil;
import org.wyf.cache.dto.DevicePlusStatusDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MonitorService {
    @Autowired
    private IBasicPileService basicPileService;
    @Autowired
    private IBasicStationService basicStationService;
    @Autowired
    private RedisUtil redisUtil;

    public ResultDTO getPileSatusList(MonitorDTO monitorDTO){
        List<MonitorVO> resultList = new ArrayList<>(32);
        List<Integer>  stations = new ArrayList<>(32);
        if(null!=monitorDTO.getStationId()){
            stations.add(monitorDTO.getStationId());
        }else{
            QueryWrapper<BasicStation> stationQueryWrapper = new QueryWrapper<>();
            stationQueryWrapper.in("org_id", Const.orgIds.get());
            List<BasicStation> stationList =  basicStationService.list(stationQueryWrapper);
            stationList.forEach(basicStation -> {
                stations.add(basicStation.getId());
            });
        }
        if(stations.size()==0){
            return new ResultDTO<>(true,resultList);
        }
        QueryWrapper<BasicPile> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name as pileName",
                "device_no as deviceNo",
                "online as online",
                "(select name from t_basic_station where t_basic_station.id = t_basic_pile.station_id) as stationName");
        queryWrapper.in("station_id",stations);

        List<Map<String,Object>> basicPileList = basicPileService.listMaps(queryWrapper);

        basicPileList.forEach(map->{

            MonitorVO monitorVO = new MonitorVO();
            monitorVO.setOnline((Integer) map.get("online"));
            monitorVO.setPileName((String) map.get("pileName"));
            monitorVO.setStationName((String) map.get("stationName"));
            List<DevicePlusStatusDTO> devicePlusStatusList = new ArrayList<>(16);
            if(1 == monitorVO.getOnline()){
                // 在线 获取插头状态
                for (String s : SmallAppletService.plugNo) {
                    if(redisUtil.exists(String.format(CacheConst.DEVICE_PLUS_STATUS,(String) map.get("deviceNo"),s))){
                        devicePlusStatusList.add(JSONUtil.toBean((String)redisUtil.get(String.format(CacheConst.DEVICE_PLUS_STATUS,(String) map.get("deviceNo"),s)),DevicePlusStatusDTO.class));
                    } else{
                        devicePlusStatusList.add(new DevicePlusStatusDTO(0));
                    }
                }
            }else{
                // 离线 就默认全是空闲
                for (String s : SmallAppletService.plugNo) {
                        devicePlusStatusList.add(new DevicePlusStatusDTO(0));
                }
            }
            monitorVO.setDeviceStatusList(devicePlusStatusList);

            resultList.add(monitorVO);
        });


        return new ResultDTO<>(true,resultList);
    }

}
