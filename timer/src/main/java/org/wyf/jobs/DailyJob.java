package org.wyf.jobs;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.wyf.balance.entity.BalanceRecord;
import org.wyf.balance.service.IBalanceRecordService;
import org.wyf.basic.entity.BasicPile;
import org.wyf.basic.entity.BasicStation;
import org.wyf.basic.service.IBasicPileService;
import org.wyf.basic.service.IBasicStationService;
import org.wyf.order.entity.OrderRecord;
import org.wyf.order.service.IOrderRecordService;
import org.wyf.stats.entity.StatsCharging;
import org.wyf.stats.entity.StatsRecharge;
import org.wyf.stats.service.IStatsChargingService;
import org.wyf.stats.service.IStatsRechargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wyf.balance.entity.BalanceRecord;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class DailyJob {

    @Autowired
    private IBalanceRecordService balanceRecordService;

    @Autowired
    private IStatsRechargeService statsRechargeService;

    @Autowired
    private IStatsChargingService statsChargingService;

    @Autowired
    private IBasicStationService basicStationService;

    @Autowired
    private IOrderRecordService orderRecordService;

    @Autowired
    private IBasicPileService basicPileService;


    private static String START = " 00:00:00";

    private static String END = " 23:59:59";

    private static BigDecimal bigDecimal30 = new BigDecimal("30");

    private static BigDecimal bigDecimal50 = new BigDecimal("50");

    private static BigDecimal bigDecimal100 = new BigDecimal("100");

    private static BigDecimal bigDecimal150 = new BigDecimal("150");

    private static BigDecimal bigDecimal200 = new BigDecimal("200");

    private static BigDecimal bigDecimal300 = new BigDecimal("300");

  //  @Scheduled(initialDelay = 1,fixedDelay = Long.MAX_VALUE)
  @Scheduled(cron = "0 0 1 * * ?")
    public void recharge(){

       DateTime yesday = DateUtil.offsetDay(new Date(),-1);
       String statsDaily = DateUtil.format(yesday,"yyyy-MM-dd");
       log.info("每日统计充值[{}]",statsDaily);
       String startTimeStr = statsDaily + START;
       String endTimeStr = statsDaily + END;
       int frequency30 = 0;
       int frequency50 = 0;
       int frequency100 = 0;
        int frequency150 = 0;
       int frequency200 = 0;
        int frequency300 = 0;
        BigDecimal amount = new BigDecimal("0");
        QueryWrapper<BalanceRecord> queryWrapper =new QueryWrapper<>();
        queryWrapper.between("create_time",startTimeStr,endTimeStr);
        queryWrapper.in("status",new Integer[]{1,2});
        List<BalanceRecord> balanceRecords = balanceRecordService.list(queryWrapper);
        for (BalanceRecord balanceRecord : balanceRecords) {
            amount = amount.add(balanceRecord.getAmount());
            if(balanceRecord.getAmount().compareTo(bigDecimal30)==0){
                frequency30++;
            }else if(balanceRecord.getAmount().compareTo(bigDecimal50)==0){
                frequency50++;
            }else if(balanceRecord.getAmount().compareTo(bigDecimal100)==0){
                frequency100++;
            }else if(balanceRecord.getAmount().compareTo(bigDecimal150)==0){
                frequency150++;
            }else if(balanceRecord.getAmount().compareTo(bigDecimal200)==0){
                frequency200++;
            }else if(balanceRecord.getAmount().compareTo(bigDecimal300)==0){
                frequency300++;
            }
        }

        StatsRecharge statsRecharge = new StatsRecharge();
        statsRecharge.setAmount(amount);
        statsRecharge.setFrequency30(frequency30);
        statsRecharge.setFrequency50(frequency50);
        statsRecharge.setFrequency150(frequency150);
        statsRecharge.setFrequency100(frequency100);
        statsRecharge.setFrequency200(frequency200);
        statsRecharge.setFrequency300(frequency300);
        statsRecharge.setStatisticsDay(statsDaily);
        statsRecharge.setIsDel(0);
        statsRechargeService.save(statsRecharge);
        log.info("每日统计充值[{}]",statsRecharge);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void charging(){

        List<StatsCharging> statsChargingList = new ArrayList<>(500);
        DateTime yesday = DateUtil.offsetDay(new Date(),-1);
        String statsDaily = DateUtil.format(yesday,"yyyy-MM-dd");
        log.info("每日统计充电[{}]",statsDaily);
        Date startTime = DateUtil.beginOfDay(yesday);
        Date endTime = DateUtil.endOfDay(yesday);
        Date startTime0002 = DateUtil.parse(statsDaily+" 00:00:00",DatePattern.NORM_DATETIME_PATTERN);
        Date endTime0002 = DateUtil.parse(statsDaily+" 01:59:59",DatePattern.NORM_DATETIME_PATTERN);

        Date startTime0204 = DateUtil.parse(statsDaily+" 02:00:00",DatePattern.NORM_DATETIME_PATTERN);
        Date endTime0204 = DateUtil.parse(statsDaily+" 03:59:59",DatePattern.NORM_DATETIME_PATTERN);

        Date startTime0406 = DateUtil.parse(statsDaily+" 04:00:00",DatePattern.NORM_DATETIME_PATTERN);
        Date endTime0406 = DateUtil.parse(statsDaily+" 05:59:59",DatePattern.NORM_DATETIME_PATTERN);

        Date startTime0608 = DateUtil.parse(statsDaily+" 06:00:00",DatePattern.NORM_DATETIME_PATTERN);
        Date endTime0608 = DateUtil.parse(statsDaily+" 07:59:59",DatePattern.NORM_DATETIME_PATTERN);

        Date startTime0810 = DateUtil.parse(statsDaily+" 08:00:00",DatePattern.NORM_DATETIME_PATTERN);
        Date endTime0810 = DateUtil.parse(statsDaily+" 09:59:59",DatePattern.NORM_DATETIME_PATTERN);

        Date startTime1012 = DateUtil.parse(statsDaily+" 10:00:00",DatePattern.NORM_DATETIME_PATTERN);
        Date endTime1012 = DateUtil.parse(statsDaily+" 11:59:59",DatePattern.NORM_DATETIME_PATTERN);

        Date startTime1214 = DateUtil.parse(statsDaily+" 12:00:00",DatePattern.NORM_DATETIME_PATTERN);
        Date endTime1214 = DateUtil.parse(statsDaily+" 13:59:59",DatePattern.NORM_DATETIME_PATTERN);

        Date startTime1416 = DateUtil.parse(statsDaily+" 14:00:00",DatePattern.NORM_DATETIME_PATTERN);
        Date endTime1416 = DateUtil.parse(statsDaily+" 15:59:59",DatePattern.NORM_DATETIME_PATTERN);

        Date startTime1618 = DateUtil.parse(statsDaily+" 16:00:00",DatePattern.NORM_DATETIME_PATTERN);
        Date endTime1618 = DateUtil.parse(statsDaily+" 17:59:59",DatePattern.NORM_DATETIME_PATTERN);

        Date startTime1820 = DateUtil.parse(statsDaily+" 18:00:00",DatePattern.NORM_DATETIME_PATTERN);
        Date endTime1820 = DateUtil.parse(statsDaily+" 19:59:59",DatePattern.NORM_DATETIME_PATTERN);

        Date startTime2022 = DateUtil.parse(statsDaily+" 20:00:00",DatePattern.NORM_DATETIME_PATTERN);
        Date endTime2022 = DateUtil.parse(statsDaily+" 21:59:59",DatePattern.NORM_DATETIME_PATTERN);

        Date startTime2224 = DateUtil.parse(statsDaily+" 22:00:00",DatePattern.NORM_DATETIME_PATTERN);
        Date endTime2224 = DateUtil.parse(statsDaily+" 23:59:59",DatePattern.NORM_DATETIME_PATTERN);

        List<BasicStation> stationList =  basicStationService.list();

        for (BasicStation basicStation : stationList) {
            log.info("站点[{}]",basicStation.getName());
            Integer stationId = basicStation.getId();
            Integer orgId = basicStation.getOrgId();
            BigDecimal amount = new BigDecimal("0");
            BigDecimal amount0002 = new BigDecimal("0");
            BigDecimal amount0204 = new BigDecimal("0");
            BigDecimal amount0406 = new BigDecimal("0");
            BigDecimal amount0608 = new BigDecimal("0");
            BigDecimal amount0810 = new BigDecimal("0");
            BigDecimal amount1012 = new BigDecimal("0");
            BigDecimal amount1214 = new BigDecimal("0");
            BigDecimal amount1416 = new BigDecimal("0");
            BigDecimal amount1618 = new BigDecimal("0");
            BigDecimal amount1820 = new BigDecimal("0");
            BigDecimal amount2022 = new BigDecimal("0");
            BigDecimal amount2224 = new BigDecimal("0");
            long holdingMins = 0 ;
            BigDecimal holdingRate = new BigDecimal("0");
            long freeMins = 0 ;

            QueryWrapper<BasicPile> pileQueryWrapper = new QueryWrapper<>();
            pileQueryWrapper.eq("station_id",stationId);
            int pileCount = basicPileService.count(pileQueryWrapper);
            if(pileCount==0){
                StatsCharging statsCharging = new StatsCharging();
                statsCharging.setAmount(amount)
                        .setAmount0002(amount0002)
                        .setAmount0204(amount0204)
                        .setAmount0406(amount0406)
                        .setAmount0608(amount0608)
                        .setAmount0810(amount0810)
                        .setAmount1012(amount1012)
                        .setAmount1214(amount1214)
                        .setAmount1416(amount1416)
                        .setAmount1618(amount1618)
                        .setAmount1820(amount1820)
                        .setAmount2022(amount2022)
                        .setAmount2224(amount2224)
                        .setFreeMins((int) freeMins)
                        .setHoldingMins((int) holdingMins)
                        .setHoldingRate(holdingRate)
                        .setOrgId(orgId)
                        .setStationId(stationId)
                        .setStatisticsDay(statsDaily)
                        .setIsDel(0);

                statsChargingList.add(statsCharging);
                continue;
            }
            int allMins = pileCount*24*60*10;



            List<OrderRecord> pileList = currentDayList(startTime,endTime,stationId);

            for (OrderRecord orderRecord : pileList) {
                //累加充电金额
                amount =  amount.add(orderRecord.getAmount());
                //累加各时段金额
                if(DateUtil.isIn(orderRecord.getChargingStartTime(),startTime0002,endTime0002)){
                    amount0002 =  amount0002.add(orderRecord.getAmount());
                }else if(DateUtil.isIn(orderRecord.getChargingStartTime(),startTime0204,endTime0204)){
                    amount0204 = amount0204.add(orderRecord.getAmount());
                }else if(DateUtil.isIn(orderRecord.getChargingStartTime(),startTime0406,endTime0406)){
                    amount0406 = amount0406.add(orderRecord.getAmount());
                }else if(DateUtil.isIn(orderRecord.getChargingStartTime(),startTime0608,endTime0608)){
                    amount0608 = amount0608.add(orderRecord.getAmount());
                }else if(DateUtil.isIn(orderRecord.getChargingStartTime(),startTime0810,endTime0810)){
                    amount0810 = amount0810.add(orderRecord.getAmount());
                }else if(DateUtil.isIn(orderRecord.getChargingStartTime(),startTime1012,endTime1012)){
                    amount1012 = amount1012.add(orderRecord.getAmount());
                }else if(DateUtil.isIn(orderRecord.getChargingStartTime(),startTime1214,endTime1214)){
                    amount1214 = amount1214.add(orderRecord.getAmount());
                }else if(DateUtil.isIn(orderRecord.getChargingStartTime(),startTime1416,endTime1416)){
                    amount1416 = amount1416.add(orderRecord.getAmount());
                }else if(DateUtil.isIn(orderRecord.getChargingStartTime(),startTime1618,endTime1618)){
                    amount1618 = amount1618.add(orderRecord.getAmount());
                }else if(DateUtil.isIn(orderRecord.getChargingStartTime(),startTime1820,endTime1820)){
                    amount1820 = amount1820.add(orderRecord.getAmount());
                }else if(DateUtil.isIn(orderRecord.getChargingStartTime(),startTime2022,endTime2022)){
                    amount2022 = amount2022.add(orderRecord.getAmount());
                }else if(DateUtil.isIn(orderRecord.getChargingStartTime(),startTime2224,endTime2224)){
                    amount2224 = amount2224.add(orderRecord.getAmount());
                }
                if(orderRecord.getChargingEndTime().getTime()>endTime.getTime()){
                    // 跨天了 占用时间 = 23：59：59-开始时间
                    holdingMins = holdingMins + DateUtil.between(orderRecord.getChargingStartTime(),endTime,DateUnit.MINUTE);
                }else{
                    // 开始时间、结束时间都在当天
                    holdingMins = holdingMins + DateUtil.between(orderRecord.getChargingStartTime(),orderRecord.getChargingEndTime(),DateUnit.MINUTE);
                }

            }

             pileList = nextDayList(startTime,endTime,stationId);

            for (OrderRecord orderRecord : pileList) {
              holdingMins = holdingMins + DateUtil.between(startTime,orderRecord.getChargingEndTime(),DateUnit.MINUTE);
            }
            // 计算空闲时间
            freeMins  = allMins - holdingMins ;
            holdingRate = new BigDecimal(holdingMins).divide(new BigDecimal(allMins),4,RoundingMode.HALF_UP);

            StatsCharging statsCharging = new StatsCharging();
            statsCharging.setAmount(amount)
                    .setAmount0002(amount0002)
                    .setAmount0204(amount0204)
                    .setAmount0406(amount0406)
                    .setAmount0608(amount0608)
                    .setAmount0810(amount0810)
                    .setAmount1012(amount1012)
                    .setAmount1214(amount1214)
                    .setAmount1416(amount1416)
                    .setAmount1618(amount1618)
                    .setAmount1820(amount1820)
                    .setAmount2022(amount2022)
                    .setAmount2224(amount2224)
                    .setFreeMins((int) freeMins)
                    .setHoldingMins((int) holdingMins)
                    .setHoldingRate(holdingRate)
                    .setOrgId(orgId)
                    .setStationId(stationId)
                    .setStatisticsDay(statsDaily)
                    .setIsDel(0);

            statsChargingList.add(statsCharging);

        }

        if(CollectionUtil.isNotEmpty(statsChargingList)){
            log.info("共保存[{}]条数据",statsChargingList.size());
            statsChargingService.saveBatch(statsChargingList);
        }else{
            log.info("无数据保存");
        }

    }

    /**
     *  查询开始时间是当日的
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param stationId  站点
     * @return 订单列表
     */
    private List<OrderRecord> currentDayList(Date startTime,Date endTime,Integer stationId){
        return list("charging_start_time",startTime,endTime,stationId);
    }

    /**
     *  查询结束时间是当日的
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param stationId  站点
     * @return 订单列表
     */
    private List<OrderRecord> nextDayList(Date startTime,Date endTime,Integer stationId){
        QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("device_no","select device_no from t_basic_pile where t_basic_pile.station_id="+stationId+" ");
        queryWrapper.eq("status",1);
        queryWrapper.eq("cmd_status",2);
        queryWrapper.between("charging_end_time",startTime,endTime);
        queryWrapper.notBetween("charging_start_time",startTime,endTime);
        List<OrderRecord> pileList = orderRecordService.list(queryWrapper);
        return pileList;
    }
    /**
     * @param column 查询字段
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param stationId  站点
     * @return 订单列表
     */
    private List<OrderRecord> list(String column,Date startTime,Date endTime,Integer stationId){
        QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("device_no","select device_no from t_basic_pile where t_basic_pile.station_id="+stationId+" ");
        queryWrapper.eq("status",1);
        queryWrapper.eq("cmd_status",2);
        queryWrapper.between(column,startTime,endTime);
        List<OrderRecord> pileList = orderRecordService.list(queryWrapper);
        return pileList;
    }

}
