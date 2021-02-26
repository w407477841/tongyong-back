package org.wyf.service;

import cn.hutool.core.date.BetweenFormater;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.wxpay.sdk.WXPay;
import org.wyf.aftersale.entity.AsIdea;
import org.wyf.aftersale.entity.AsMalfunction;
import org.wyf.aftersale.service.IAsIdeaService;
import org.wyf.aftersale.service.IAsMalfunctionService;
import org.wyf.balance.entity.BalanceRecord;
import org.wyf.balance.service.IBalanceRecordService;
import org.wyf.basic.entity.BasicCard;
import org.wyf.basic.entity.BasicPile;
import org.wyf.basic.entity.BasicQrcode;
import org.wyf.basic.service.IBasicCardService;
import org.wyf.basic.service.IBasicPileService;
import org.wyf.basic.service.IBasicQrcodeService;
import org.wyf.cache.CacheConst;
import org.wyf.cache.RedisUtil;
import org.wyf.cache.dto.DevicePlusStatusDTO;
import org.wyf.common.constant.Const;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.DataDTO;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.common.utils.RequestUtil;
import org.wyf.config.WeChatConfig;
import org.wyf.config.properties.WeChatProperties;
import org.wyf.order.entity.OrderRecord;
import org.wyf.order.service.IOrderRecordService;
import org.wyf.security.filter.JWTAuthenticationFilter;
import org.wyf.system.ConstSystem;
import org.wyf.system.model.User;
import org.wyf.system.service.service.IUserService;
import org.wyf.vo.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.wyf.balance.entity.BalanceRecord;
import org.wyf.cache.CacheConst;
import org.wyf.cache.RedisUtil;
import org.wyf.cache.dto.DevicePlusStatusDTO;
import org.wyf.config.WeChatConfig;
import org.wyf.security.filter.JWTAuthenticationFilter;
import org.wyf.system.ConstSystem;
import org.wyf.system.model.User;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.wyf.security.filter.JWTAuthenticationFilter.*;

@Component
@Slf4j
public class SmallAppletService {
    @Autowired
    private IUserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IOrderRecordService orderRecordService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IBasicPileService basicPileService;
    @Autowired
    private IBalanceRecordService balanceRecordService;
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private WeChatConfig weChatConfig;
    @Autowired
    private IBasicCardService basicCardService;
    @Autowired
    private IBasicQrcodeService basicQrcodeService;
    @Autowired
    private IAsIdeaService asIdeaService;
    @Autowired
    private IAsMalfunctionService asMalfunctionService;

    private static final String CACHE_KEY =  "tongyong:refresh-token:";
    private static final String SUCCESS = "<xml><return_code><![CDATA[SUCCESS]]></return_code> <return_msg><![CDATA[OK]]></return_msg></xml>";

    private static final String ERROR = "<xml><return_code><![CDATA[FAIL]]></return_code> <return_msg><![CDATA[OK]]></return_msg></xml>";

    private static final String NOTIFY_URL_PAY =  "https://xiaolv.v2x.net.cn:18090/wechat/smallapplet/payNotify";

    private static final String NOTIFY_URL_BALANCE =  "https://xiaolv.v2x.net.cn:18090/wechat/smallapplet/balanceNotify";
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO register(RegisterDTO registerDTO){

        User user  =new User();
        user.setCode("HH"+registerDTO.getPhone());
        user.setName("HH"+registerDTO.getPhone());
        user.setPhone(registerDTO.getPhone());
        user.setPassword(passwordEncoder.encode("123456"));
        user.setFlag(2);
        user.setBalance(new BigDecimal("0"));
       // user.setWxname(registerDTO.getWxname());
        //绑定到顶级组织下
        user.setOrgId(1);
        // 绑定管理员
        user.setCreateUser(1);

        userService.save(user);

        Claims claims =new DefaultClaims();
        Long lasttime=System.currentTimeMillis() + 180*24*60*60*1000L;
        //持有者
        claims.put("sub",user.getName());
        //过期时间
        claims.put("exp", lasttime);
        //权限
        claims.put("auths",null);

        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(lasttime))
                .signWith(SignatureAlgorithm.HS512, JWTAuthenticationFilter.JWT_SECRET)
                .compact();

        //设置一个 refresh_token;
        redisUtil.set(CACHE_KEY+registerDTO.getPhone(),token,180*24*60L);

        return new ResultDTO<>(true, JWTAuthenticationFilter.JTW_TOKEN_HEAD+token,"成功");
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultDTO login(RegisterDTO registerDTO){

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",registerDTO.getPhone());
        User user = userService.getOne(queryWrapper);


        Claims claims =new DefaultClaims();
        Long lasttime=System.currentTimeMillis() + 180*24*60*60*1000L;
        //持有者
        claims.put("sub",user.getName());
        //过期时间
        claims.put("exp", lasttime);
        //权限
        claims.put("auths",null);

        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(lasttime))
                .signWith(SignatureAlgorithm.HS512, JWTAuthenticationFilter.JWT_SECRET)
                .compact();

        //设置一个 refresh_token;
        redisUtil.set(CACHE_KEY+registerDTO.getPhone(),token,180*24*60L);

        return new ResultDTO(true, JWTAuthenticationFilter.JTW_TOKEN_HEAD+token,"成功");
    }


    @Transactional(rollbackFor = Exception.class)
    public ResultDTO refreshToken(RegisterDTO registerDTO){

        Claims claims = null;

        try {
            //解析获得 权鉴
            claims = Jwts.parser().setSigningKey(JWTAuthenticationFilter.JWT_SECRET)
                    .parseClaimsJws(registerDTO.getRefreshToken()).getBody();

        } catch (ExpiredJwtException e) {
            e.printStackTrace();
        }
        if (claims == null) {
            return ResultDTO.factory(ResultCodeEnum.TOKEN_WRONG);
        }

         Long expirationTime =   claims.get("exp",Long.class);
         long currentTime = System.currentTimeMillis();
         if(expirationTime<currentTime){
             // 超时了
             return ResultDTO.factory(ResultCodeEnum.TOKEN_TIMEOUT);
         }
        if(!redisUtil.exists(CACHE_KEY+registerDTO.getPhone())){
             // 缓存中已失效
            return ResultDTO.factory(ResultCodeEnum.TOKEN_TIMEOUT);
        }

        String oldTtoken = (String) redisUtil.get(CACHE_KEY+registerDTO.getPhone());

         if(!registerDTO.getRefreshToken().equals(oldTtoken)){
             // 两个token不一致
             return ResultDTO.factory(ResultCodeEnum.INVALID_TOKEN);
         }

        claims =new DefaultClaims();
        Long lasttime=System.currentTimeMillis() + JWTAuthenticationFilter.JWT_EXP;
        //持有者
        claims.put("sub","HH"+registerDTO.getPhone());
        //过期时间
        claims.put("exp", lasttime);
        //权限
        claims.put("auths", null);
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(lasttime))
                .signWith(SignatureAlgorithm.HS512, JWTAuthenticationFilter.JWT_SECRET)
                .compact();


        return new ResultDTO(true,token,"成功");

    }

    /**
     * 用户绑定虚拟卡
     * @param cardDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO bindCard(BindCardDTO cardDTO){
        User user =   ConstSystem.currUser.get();
        String key = String.format(CacheConst.CARD_PHONE_KEY,cardDTO.getCardNo().substring(0,8)) ;
        redisUtil.set(key,user.getPhone());

        QueryWrapper<BasicCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("card_no",cardDTO.getCardNo());

        //判断虚拟卡号存不存在
        BasicCard basicCard = basicCardService.getOne(queryWrapper);
        if (basicCard == null) {
            return ResultDTO.factory(ResultCodeEnum.NO_CARDNO);
        }

        //判断用户id是否为空或者为-1
        if (basicCard.getUserId() != null && basicCard.getUserId() != -1) {
            return ResultDTO.factory(ResultCodeEnum.USERID_BINDED);
        }

        basicCard.setUserId(user.getId());
        basicCard.setStatus(1);
        basicCardService.update(basicCard,queryWrapper);
        return new ResultDTO(true);
    }

    /**
     * 用户解绑虚拟卡
     * @param cardDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO unbindCard(BindCardDTO cardDTO){
        User user =   ConstSystem.currUser.get();
        String key = String.format(CacheConst.CARD_PHONE_KEY,cardDTO.getCardNo().substring(0,8)) ;
        redisUtil.remove(key);

        QueryWrapper<BasicCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("card_no",cardDTO.getCardNo());

        //判断虚拟卡号存不存在
        BasicCard basicCard = basicCardService.getOne(queryWrapper);
        if (basicCard == null) {
            return ResultDTO.factory(ResultCodeEnum.NO_CARDNO);
        }

        //获取的用户id与库里不匹配
        if (!user.getId().equals(basicCard.getUserId())) {
            return ResultDTO.factory(ResultCodeEnum.NO_WATCH_USERID);
        }

        basicCardService.updateUserid(cardDTO.getCardNo());
        return new ResultDTO(true);
    }

    /**
     * 新增 意见反馈
     * @param body
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO insertAsIdea(AsIdea body){
        User user = ConstSystem.currUser.get();
        AsIdea idea = new AsIdea();
        idea.setPhone(user.getPhone());
        idea.setStatus(0);
        idea.setFeedbackStatus(0);
        idea.setIdeaContent(body.getIdeaContent());

        if(asIdeaService.save(idea)){
            return ResultDTO.resultFactory(OperationEnum.INSERT_SUCCESS);
        }else{
            return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
        }
    }

    /**
     * 查询 意见反馈
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<DataDTO<List<AsIdeaVO>>> selectAsIdea(@RequestBody RequestDTO<AsIdea> t) {
        User user =  ConstSystem.currUser.get();
        Page<AsIdea> page = new Page<>(t.getPageNum(), t.getPageSize());
        QueryWrapper<AsIdea> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","phone","idea_content as ideaContent","feedback_content as feedbackContent",
                "status","feedback_status as feedbackStatus","comments");

        queryWrapper.eq("phone",user.getPhone());
        queryWrapper.eq("is_del",0);
        queryWrapper.orderByDesc("create_time");

        List<AsIdea> idea = asIdeaService.page(page,queryWrapper).getRecords();
        List<AsIdeaVO> ideaVO = new ArrayList<>();
        for (AsIdea asIdea : idea) {
            AsIdeaVO asIdeaVO = new AsIdeaVO();
            asIdeaVO.setId(asIdea.getId());
            asIdeaVO.setPhone(asIdea.getPhone());
            asIdeaVO.setIdeaContent(asIdea.getIdeaContent());
            asIdeaVO.setFeedbackContent(asIdea.getFeedbackContent());
            asIdeaVO.setStatus(asIdea.getStatus());
            asIdeaVO.setFeedbackStatus(asIdea.getFeedbackStatus());
            asIdeaVO.setComments(asIdea.getComments());
            ideaVO.add(asIdeaVO);
        }

        return new ResultDTO<>(true, DataDTO.factory(ideaVO, page.getTotal()));
    }

    /**
     * 更新 意见反馈解决状态
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO updateAsIdea(@RequestBody RequestDTO<AsIdea> t) {
        if(asIdeaService.updateById(t.getBody())){
            return ResultDTO.resultFactory(OperationEnum.SUBMIT_SUCCESS);
        }else{
            return ResultDTO.resultFactory(OperationEnum.SUBMIT_ERROR);
        }
    }

    /**
     * 新增 故障报修
     * @param body
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO insertAsMalfunction(AsMalfunctionVO body){
        User user = ConstSystem.currUser.get();
        AsMalfunction malfunction = new AsMalfunction();
        malfunction.setDeviceNo(body.getDeviceNo());
        malfunction.setPlugNo(body.getPlugNo());
        malfunction.setReportTime(DateUtil.parse(body.getReportTime(),"yyyy-MM-dd HH:mm:ss"));
        malfunction.setMalfunctionContent(body.getMalfunctionContent());
        malfunction.setStatus(0);
        malfunction.setFeedbackStatus(0);
        malfunction.setPhone(user.getPhone());

        if(asMalfunctionService.save(malfunction)){
            return ResultDTO.resultFactory(OperationEnum.INSERT_SUCCESS);
        }else{
            return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
        }
    }

    /**
     * 查询 故障报修
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<DataDTO<List<AsMalfunctionVO>>> selectAsMalfunction(@RequestBody RequestDTO<AsMalfunction> t) {
        User user =  ConstSystem.currUser.get();
        Page<AsMalfunction> page = new Page<>(t.getPageNum(), t.getPageSize());
        QueryWrapper<AsMalfunction> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","phone","device_no as deviceNo","plug_no as plugNo","report_time as reportTime",
                "repair_time as repairTime","malfunction_content as malfunctionContent",
                "feedback_content as feedbackContent","status","feedback_status as feedbackStatus","comments");

        queryWrapper.eq("phone",user.getPhone());
        queryWrapper.eq("is_del",0);
        queryWrapper.orderByDesc("create_time");

        List<AsMalfunction> function = asMalfunctionService.page(page,queryWrapper).getRecords();
        List<AsMalfunctionVO> functionVO = new ArrayList<>();
        for (AsMalfunction asMalfunction : function) {
            AsMalfunctionVO asMalfunctionVO = new AsMalfunctionVO();
            asMalfunctionVO.setId(asMalfunction.getId());
            asMalfunctionVO.setPhone(asMalfunction.getPhone());
            asMalfunctionVO.setDeviceNo(asMalfunction.getDeviceNo());
            asMalfunctionVO.setPlugNo(asMalfunction.getPlugNo());
            asMalfunctionVO.setReportTime(DateUtil.format(asMalfunction.getReportTime(), "yyyy-MM-dd HH:mm:ss"));
            asMalfunctionVO.setRepairTime(DateUtil.format(asMalfunction.getRepairTime(), "yyyy-MM-dd HH:mm:ss"));
            asMalfunctionVO.setMalfunctionContent(asMalfunction.getMalfunctionContent());
            asMalfunctionVO.setFeedbackContent(asMalfunction.getFeedbackContent());
            asMalfunctionVO.setStatus(asMalfunction.getStatus());
            asMalfunctionVO.setFeedbackStatus(asMalfunction.getFeedbackStatus());
            asMalfunctionVO.setComments(asMalfunction.getComments());
            functionVO.add(asMalfunctionVO);
        }
        return new ResultDTO<>(true, DataDTO.factory(functionVO, page.getTotal()));
    }

    /**
     * 更新 故障报修解决状态
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO updateAsMalfunction(@RequestBody RequestDTO<AsMalfunction> t) {
        if(asMalfunctionService.updateById(t.getBody())){
            return ResultDTO.resultFactory(OperationEnum.SUBMIT_SUCCESS);
        }else{
            return ResultDTO.resultFactory(OperationEnum.SUBMIT_ERROR);
        }
    }

    /**
     * 获取订单详情
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<DataDTO<List<OrderRecoderVO>>> getOrderRecord(@RequestBody RequestDTO<OrderRecord> t) {
        User user =  ConstSystem.currUser.get();
        Page<OrderRecord> page = new Page<>(t.getPageNum(), t.getPageSize());
        QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("order_no as orderNo","device_no as deviceNo","plug_no as plugNo","card_no as cardNo",
                "order_tag as orderTag","amount as amount","cmd_status as cmdStatus","charging_start_time as chargingStartTime",
                "charging_end_time as chargingEndTime");

        queryWrapper.eq("phone",user.getPhone());
        queryWrapper.eq("status",1);
        queryWrapper.orderByDesc("pay_time");

        List<OrderRecord> ts = orderRecordService.page(page,queryWrapper).getRecords();
        List<OrderRecoderVO> orVO = new ArrayList<>();
        for(OrderRecord or : ts){
            OrderRecoderVO ovo = new OrderRecoderVO();
            BeanUtils.copyProperties(or,ovo,"chargingStartTime","chargingEndTime");
            if (or.getChargingStartTime() == null) {
                ovo.setChargingStartTime("-");
                ovo.setElapsedTime("-");
            } else {
                String startTime = DateUtil.format(or.getChargingStartTime(), "yyyy年MM月dd日 HH时mm分");
                ovo.setChargingStartTime(startTime);
            }
            if (or.getChargingEndTime() == null) {
                ovo.setChargingEndTime("-");
                ovo.setElapsedTime("-");
            } else {
                String endTime = DateUtil.format(or.getChargingEndTime(), "yyyy年MM月dd日 HH时mm分");
                ovo.setChargingEndTime(endTime);
            }
            if (or.getChargingStartTime() != null && or.getChargingEndTime() != null) {
                //计算时间差
                long betweenDay = DateUtil.between(or.getChargingStartTime(), or.getChargingEndTime(), DateUnit.MS);
                //格式化时间差 Level.MINUTE表示精确到分
                String elapsedTime = DateUtil.formatBetween(betweenDay, BetweenFormater.Level.MINUTE);
                ovo.setElapsedTime(elapsedTime);
            }
            orVO.add(ovo);
        }
        return new ResultDTO<>(true, DataDTO.factory(orVO, page.getTotal()));
    }

    /**
     * 获取卡列表
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<List<BindCardDTO>> getCardno() {
        User user =  ConstSystem.currUser.get();
        QueryWrapper<BasicCard> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("card_no as cardNo");
        queryWrapper.eq("user_id",user.getId());
        queryWrapper.orderByDesc("create_time");

        List<BasicCard> bc = basicCardService.list(queryWrapper);
        List<BindCardDTO> bddto = new ArrayList<>();
        for (BasicCard basicCard : bc) {
            BindCardDTO bcdto = new BindCardDTO();
            bcdto.setCardNo(basicCard.getCardNo());
            bddto.add(bcdto);
        }
        return new ResultDTO(true, bddto);
    }

    /**
     * 获取订单号
     * @return
     */
    public String getOrderNO(){
        // 分布式锁
        RLock lock = redissonClient.getLock(CacheConst.ORDER_NO_LOCK_KEY);
        boolean res = redisUtil.lock(lock,CacheConst.ORDER_NO_LOCK_KEY);
        // 是否是第一次
        int count = 1;
        if(res){
            try {
                if(redisUtil.exists(CacheConst.ORDER_COUNT_KEY)){
                    //
                    count = count + (Integer)redisUtil.get(CacheConst.ORDER_COUNT_KEY);
                }
                redisUtil.set(CacheConst.ORDER_COUNT_KEY,count);
            }finally {
                lock.unlock();
            }
        }else{
            // 没拿到锁
            return null;
        }

        // 订单号 yyyyMMdd+   订单总量(10位)
        String orderNo = DateUtil.format(new Date(),"yyyyMMdd") + StrUtil.fillBefore(""+count,'0',10);
        return orderNo;
    }

    public OrderRecord payment(MobilePaymentChargingDTO dto,String wxOrderNO,int status,int orderTag){

        User user =   ConstSystem.currUser.get();

        OrderRecord orderRecord = new OrderRecord();
        orderRecord.setPayTime(new Date());
        orderRecord.setStatus(status);
        orderRecord.setAmount(dto.getAmount());
        orderRecord.setCmdStatus(0);
        orderRecord.setChargingStatus(0);
        orderRecord.setOrderTag(orderTag);
        orderRecord.setDeviceNo(dto.getDeviceNo());
        orderRecord.setOrderNo(dto.getOrderNo());
        orderRecord.setPlugNo(dto.getPlug());
        orderRecord.setPhone(user.getPhone());
        orderRecord.setWxOrderNo(wxOrderNO);
        orderRecordService.save(orderRecord);

        return orderRecord;
    }


    @Transactional(rollbackFor = Exception.class)
    public OrderRecord wxPayment(MobilePaymentChargingDTO dto,String wxOrderNO){

        return payment(dto,wxOrderNO,3,1);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultDTO udpWxPayment(MobilePaymentChargingDTO dto){

        QueryWrapper<OrderRecord> queryWrapper  = new QueryWrapper<>();
        queryWrapper.eq("order_no",dto.getOrderNo());

        OrderRecord orderRecord = orderRecordService.getOne(queryWrapper);
        if(null == orderRecord){
            return new ResultDTO<>(false, 4000, "订单号【" + dto.getOrderNo() + "】不存在，请联系客服");
        }

        if(3!=orderRecord.getStatus()){
            return new ResultDTO<>(false, 4000, "订单号【" + dto.getOrderNo() + "】状态不一样，请联系客服");
        }

        orderRecord.setStatus(1);

        orderRecordService.updateById(orderRecord);

        if (!redisUtil.exists(CacheConst.NETTY_ONLINE_KEY)) {
            log.info("[{}] A7指令调用失败[Netty服务器不在线]",Const.REQUEST_SERIAL_NUMBER.get());
            return new ResultDTO<>(false, 4000, "订单号【" + orderRecord.getOrderNo() + "】开启失败，请联系客服");
        }
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setCmd("A7");
        deviceDTO.setOrderNo(orderRecord.getOrderNo());
        deviceDTO.setPlug(orderRecord.getPlugNo());
        deviceDTO.setDeviceNo(orderRecord.getDeviceNo());
        // 设置 数据体   orderNoHex + jifenHex+ 01F60B   钱转积分 1：100
        int jifen = orderRecord.getAmount().multiply(new BigDecimal("100")).intValue();
        String orderNoHex = StrUtil.fillBefore(Long.toHexString(Long.parseLong(orderRecord.getOrderNo())), '0', 16);
        String jifenHex = StrUtil.fillBefore(Integer.toHexString(jifen), '0', 6);
        String zongjifen = "01F60B";
        deviceDTO.setData((orderNoHex + jifenHex + zongjifen).toUpperCase());
        RequestDTO<DeviceDTO> reqDto = new RequestDTO<>();
        reqDto.setBody(deviceDTO);

        String result = HttpUtil.post((String) redisUtil.get(CacheConst.NETTY_ONLINE_KEY)+"/remote/action", JSONUtil.toJsonStr(reqDto));
        ResultDTO resultDTO = JSONUtil.toBean(result, ResultDTO.class);

        if (200 == resultDTO.getCode()) {
            log.info("[{}] A7指令调用成功[{}]",Const.REQUEST_SERIAL_NUMBER.get(), deviceDTO.toString());
            return ResultDTO.success("成功");
        } else {
            log.info("[{}] A7指令调用失败[{}][{}]",Const.REQUEST_SERIAL_NUMBER.get(), deviceDTO.toString(),result);
            return new ResultDTO<>(false, 4000, "订单号【" + orderRecord.getOrderNo() + "】开启失败，请联系客服");
        }
    }


   // @Transactional(rollbackFor = Exception.class)
    public String udpWxPaymentStr(MobilePaymentChargingDTO dto){

        QueryWrapper<OrderRecord> queryWrapper  = new QueryWrapper<>();
        queryWrapper.eq("order_no",dto.getOrderNo());

        OrderRecord orderRecord = orderRecordService.getOne(queryWrapper);
        if(null == orderRecord){
            log.info( "订单号【" + dto.getOrderNo() + "】不存在，请联系客服");
            return SUCCESS;
        }

        if(3!=orderRecord.getStatus()){
            log.info( "订单号【" + dto.getOrderNo() + "】状态不一样，请联系客服");
            return SUCCESS;
        }

        orderRecord.setStatus(1);

        orderRecordService.updateById(orderRecord);

        if (!redisUtil.exists(CacheConst.NETTY_ONLINE_KEY)) {
            log.info("A7指令调用失败[Netty服务器不在线]");
            return ERROR;
        }
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setCmd("A7");
        deviceDTO.setOrderNo(orderRecord.getOrderNo());
        deviceDTO.setPlug(orderRecord.getPlugNo());
        deviceDTO.setDeviceNo(orderRecord.getDeviceNo());
        // 设置 数据体   orderNoHex + jifenHex+ 01F60B   钱转积分 1：100
        int jifen = orderRecord.getAmount().multiply(new BigDecimal("100")).intValue();
        String orderNoHex = StrUtil.fillBefore(Long.toHexString(Long.parseLong(orderRecord.getOrderNo())), '0', 16);
        String jifenHex = StrUtil.fillBefore(Integer.toHexString(jifen), '0', 6);
        String zongjifen = "01F60B";
        deviceDTO.setData((orderNoHex + jifenHex + zongjifen).toUpperCase());
        RequestDTO<DeviceDTO> reqDto = new RequestDTO<>();
        reqDto.setBody(deviceDTO);

        String result = HttpUtil.post((String) redisUtil.get(CacheConst.NETTY_ONLINE_KEY)+"/remote/action", JSONUtil.toJsonStr(reqDto));
        ResultDTO resultDTO = JSONUtil.toBean(result, ResultDTO.class);

        if (200 == resultDTO.getCode()) {
            log.info("A7指令调用成功[{}]", deviceDTO.toString());
            return SUCCESS;
        } else {
            log.info("A7指令调用失败[{}][{}]", deviceDTO.toString(),result);
            return ERROR;
        }
    }



    public ResultDTO validDevice(String deviceNo){
        QueryWrapper<BasicPile> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("device_no",deviceNo);
        BasicPile basicPile = basicPileService.getOne(queryWrapper);
        if(basicPile== null){
            return ResultDTO.factory(ResultCodeEnum.NO_PILE);
        }
        if(0==basicPile.getOnline()){
            return ResultDTO.factory(ResultCodeEnum.PILE_OFFLINE);
        }
        return null;
    }

    public static String [] plugNo = {"01","02","03","04","05","06","07","08","09","0A"};

    public ResultDTO<DeviceStatusVO> getPileStatus(String deviceNo){
        QueryWrapper<BasicPile> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("device_no",deviceNo);
        BasicPile basicPile = basicPileService.getOne(queryWrapper);
        if(basicPile== null){
            return ResultDTO.factory(ResultCodeEnum.NO_PILE);
        }
        DeviceStatusVO   deviceStatusVO= new DeviceStatusVO();
        deviceStatusVO.setDeviceNo(deviceNo);
        deviceStatusVO.setStatus(basicPile.getOnline());
        List<Integer> plugstatuses =new ArrayList<>(32);
        DevicePlusStatusDTO devicePlusStatusDTO;
        for (String s : plugNo) {
            if(redisUtil.exists(String.format(CacheConst.DEVICE_PLUS_STATUS,deviceNo,s))){
                devicePlusStatusDTO  = JSONUtil.toBean((String)redisUtil.get(String.format(CacheConst.DEVICE_PLUS_STATUS,deviceNo,s)),DevicePlusStatusDTO.class);
                plugstatuses.add(devicePlusStatusDTO.getStatus());
            } else{
                plugstatuses.add(0);
            }
        }
        deviceStatusVO.setPlugs(plugstatuses);
        return new ResultDTO<>(true,deviceStatusVO);
    }


    public boolean exist(String orderNo){

        QueryWrapper<OrderRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no",orderNo);
        return orderRecordService.count(queryWrapper)>0;

    }

    /**
     * 余额充值
     * @param dto
     * @return
     */
    public ResultDTO addBalance(BalanceDTO dto){
        String key = String.format(CacheConst.ORDER_LOCK_KEY,dto.getPhone());
        RLock lock =  redissonClient.getLock(key);
        boolean res =redisUtil.lock(lock,key);
        try {

        if(res){
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("phone",dto.getPhone());
            User user =  userService.getOne(userQueryWrapper);
            if(null == user){
                log.info("[{}] 余额充值[{}]手机号不存在",Const.REQUEST_SERIAL_NUMBER.get(),dto.getPhone());
                return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
            }
            QueryWrapper<BalanceRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_no",dto.getOrderNo());
            queryWrapper.eq("phone",dto.getPhone());
            BalanceRecord balanceRecord =  balanceRecordService.getOne(queryWrapper);
            if(balanceRecord == null){
                log.info("[{}] 余额充值[{}]订单不存在",Const.REQUEST_SERIAL_NUMBER.get(),dto.getOrderNo());
                return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
            }
            if(1==balanceRecord.getStatus()){
                log.info("[{}] 余额充值[{}]订单已完成",Const.REQUEST_SERIAL_NUMBER.get(),dto.getOrderNo());
                return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
            }
            balanceRecord.setStatus(1);
            BigDecimal oldAmount =  balanceRecord.getAmount();
            BigDecimal nowAmount =  dto.getAmount();
            if(oldAmount.compareTo(nowAmount)!= 0){
                log.info("[{}] 余额充值[{}]金额不匹配[原来{},当前{}]",Const.REQUEST_SERIAL_NUMBER.get(),dto.getOrderNo(),oldAmount,nowAmount);
                return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
            }
            if(user.getBalance()==null){
                user.setBalance(nowAmount);
            }else{
                user.setBalance(nowAmount.add(user.getBalance()));
            }

            balanceRecordService.updateById(balanceRecord);
            userService.updateById(user);
            return new ResultDTO(true);
        }else{
            log.info("[{}] 余额充值[{}]未拿到用户锁",Const.REQUEST_SERIAL_NUMBER.get(),dto.getPhone());
            return ResultDTO.factory(ResultCodeEnum.SERVER_BUSY);
        }

        }finally {
            if(res){
                lock.unlock();
            }
        }
    }

    /**
     *  余额支付
     * @param dto
     * @return
     */
    public ResultDTO  payWithBalance(MobilePaymentChargingDTO dto){
        String phone  = ConstSystem.currUser.get().getPhone();

        String key = String.format(CacheConst.ORDER_LOCK_KEY,phone);
        String plugkey = String.format(CacheConst.PLUG_LOCK_KEY,dto.getDeviceNo(),dto.getPlug());
        RLock lock =  redissonClient.getLock(key);
        RLock pluglock =  redissonClient.getLock(plugkey);

        RedissonMultiLock mutiLock = new RedissonMultiLock(lock, pluglock);

        boolean res = false;
        try {
            res = mutiLock.tryLock(5, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {

            if(res){
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("phone",phone);
                User user =  userService.getOne(userQueryWrapper);
                if(null == user){
                    log.info("[{}] 余额支付[{}]手机号不存在",Const.REQUEST_SERIAL_NUMBER.get(),phone);
                    return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
                }

                // 添加充电记录

                BigDecimal oldAmount =  user.getBalance();
                if(user.getBalance()==null){
                    user.setBalance(new BigDecimal("0"));
                }
                BigDecimal nowAmount =  dto.getAmount();
                if(oldAmount.compareTo(nowAmount)< 0){
                    log.info("[{}] 余额支付[{}]余额不足[账户余额{},订单金额{}]",Const.REQUEST_SERIAL_NUMBER.get(),dto.getOrderNo(),oldAmount,nowAmount);
                    return ResultDTO.factory(ResultCodeEnum.NOT_ENOUGH_BALANCE);
                }

                user.setBalance(oldAmount.subtract(nowAmount));


                // todo 调用netty
                if (!redisUtil.exists(CacheConst.NETTY_ONLINE_KEY)) {
                    log.info("[{}] A7指令调用失败[Netty服务器不在线]",Const.REQUEST_SERIAL_NUMBER.get());
                    return ResultDTO.factory(ResultCodeEnum.SERVER_BUSY);
                }

                // 判断有没有没占用
                DevicePlusStatusDTO devicePlusStatusDTO;
                if(redisUtil.exists(String.format(CacheConst.DEVICE_PLUS_STATUS, dto.getDeviceNo(), dto.getPlug()))){
                    devicePlusStatusDTO = JSONUtil.toBean((String) redisUtil.get(String.format(CacheConst.DEVICE_PLUS_STATUS, dto.getDeviceNo(), dto.getPlug())), DevicePlusStatusDTO.class);
                }else{
                    devicePlusStatusDTO = new DevicePlusStatusDTO();
                    devicePlusStatusDTO.setStatus(0);
                }
                // 插头占用/故障提前退出
                if (1 == devicePlusStatusDTO.getStatus()) {
                    return ResultDTO.factory(ResultCodeEnum.REMOTE_PLUG_OCCUPIED);
                } else if (2 == devicePlusStatusDTO.getStatus()) {
                    return ResultDTO.factory(ResultCodeEnum.REMOTE_PLUG_ERROR);
                }

                // 判断一个状态位，如果不存在，说明没有被操作过这个插头/操作过但是失败了
                if (redisUtil.exists(String.format(CacheConst.MOBLIE_PAYMENT_STATUS_KEY, dto.getDeviceNo(), dto.getPlug()))) {
                    // 上次的流程还未跑完
                    return ResultDTO.factory(ResultCodeEnum.SERVER_BUSY);
                } else {
                    // 确保流程跑完/ 超过60秒
                    redisUtil.set(String.format(CacheConst.MOBLIE_PAYMENT_STATUS_KEY, dto.getDeviceNo(), dto.getPlug()), 1, 60L, TimeUnit.SECONDS);
                }

                // 生成订单号
                String orderNo = this.getOrderNO();
                if (null == orderNo) {
                    log.info("[{}] 订单号获取失败",Const.REQUEST_SERIAL_NUMBER.get());
                    return ResultDTO.factory(ResultCodeEnum.SERVER_BUSY);
                }
                // 修改状态
                devicePlusStatusDTO.setOrderNo(orderNo);
                //   devicePlusStatusDTO.setStatus(1);
                redisUtil.set(String.format(CacheConst.DEVICE_PLUS_STATUS, dto.getDeviceNo(), dto.getPlug()), JSONUtil.toJsonStr(devicePlusStatusDTO));

                dto.setOrderNo(orderNo);
                // 添加订单
                OrderRecord orderRecord =   this.payment(dto,"",3,3);

                DeviceDTO deviceDTO = new DeviceDTO();
                deviceDTO.setCmd("A7");
                deviceDTO.setOrderNo(orderNo);
                deviceDTO.setPlug(dto.getPlug());
                deviceDTO.setDeviceNo(dto.getDeviceNo());
                // 设置 数据体   orderNoHex + jifenHex+ 01F60B   钱转积分 1：100
                int jifen = dto.getAmount().multiply(new BigDecimal("100")).intValue();
                String orderNoHex = StrUtil.fillBefore(Long.toHexString(Long.parseLong(orderNo)), '0', 16);
                String jifenHex = StrUtil.fillBefore(Integer.toHexString(jifen), '0', 6);
                String zongjifen = "01F60B";
                deviceDTO.setData((orderNoHex + jifenHex + zongjifen).toUpperCase());
                RequestDTO<DeviceDTO> reqDto = new RequestDTO<>();
                reqDto.setBody(deviceDTO);

                String result = HttpUtil.post((String) redisUtil.get(CacheConst.NETTY_ONLINE_KEY)+"/remote/action", JSONUtil.toJsonStr(reqDto));
                ResultDTO resultDTO = JSONUtil.toBean(result, ResultDTO.class);

                if (200 == resultDTO.getCode()) {
                    log.info("[{}] A7指令调用成功[{}]",Const.REQUEST_SERIAL_NUMBER.get(), deviceDTO.toString());
                    userService.updateById(user);
                    orderRecord.setStatus(1);
                    orderRecordService.updateById(orderRecord);
                    return new ResultDTO(true);
                } else {
                    log.info("[{}] A7指令调用失败[{}][{}]",Const.REQUEST_SERIAL_NUMBER.get(), deviceDTO.toString(),result);
                    return new ResultDTO(false);
                }


            }else{
                log.info("[{}] 余额支付[{}]未拿到用户锁",Const.REQUEST_SERIAL_NUMBER.get(),phone);
                return ResultDTO.factory(ResultCodeEnum.SERVER_BUSY);
            }

        }finally {
            if(res){
                mutiLock.unlock();
            }
        }

    }

    /**
     * 换取 openid
     * @param code
     */
    public String jscode2session(String code){
        Map<String,Object> params= new HashMap<>();
        params.put("appid",weChatConfig.getAppID());
        params.put("secret",weChatProperties.getSecret());
        params.put("js_code",code);
        params.put("grant_type","authorization_code");

        return  HttpUtil.get(weChatProperties.getJscode2sessionUrl(),params);
    }

    /**
     * 统一下单
     * @param orderNo 内部订单号
     * @param amount  金额
     */
    public Map<String, String> unifiedorder(String orderNo,BigDecimal amount,String openid,String notifyUrl)throws Exception{

        ServletRequestAttributes ra= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request =  ra.getRequest();
        String ip = RequestUtil.getRealIp(request);
        WXPay wxpay = new WXPay(weChatConfig,null,true,weChatProperties.isUseSandbox());
        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "充值");
        data.put("out_trade_no", orderNo);
        //data.put("device_info", "");
        data.put("fee_type", "CNY");
        data.put("total_fee", amount.multiply(new BigDecimal("100")).intValue()+"");
        data.put("spbill_create_ip", ip);
        data.put("openid",openid);
        data.put("notify_url", notifyUrl);
        // 此处指定为扫码支付
        data.put("trade_type", "JSAPI");

        //data.put("product_id", "1");

        return wxpay.unifiedOrder(data);
    }

    /**
     * 退款
     * @param balanceRecord
     * @return
     * @throws Exception
     */
    private Map<String, String> refund(BalanceRecord balanceRecord,BigDecimal amount)throws Exception{


        WXPay wxpay = new WXPay(weChatConfig,null,true,weChatProperties.isUseSandbox());
        Map<String, String> data = new HashMap<>();

        data.put("out_trade_no", balanceRecord.getOrderNo());
        data.put("out_refund_no", "TD"+balanceRecord.getOrderNo()+RandomUtil.randomNumbers(4));
        data.put("total_fee", balanceRecord.getAmount().multiply(new BigDecimal("100")).intValue()+"");
        data.put("refund_fee", amount.multiply(new BigDecimal("100")).intValue()+"");
        data.put("refund_desc", "退款");

       // data.put("notify_url", SmallAppletAPI.NOTIFY_URL_REFUND);

        return wxpay.refund(data);
    }

    /**
     *  退款
     * @param orderNo
     * @return
     */
    public ResultDTO refund(String orderNo){
        String phone  = ConstSystem.currUser.get().getPhone();
        String key = String.format(CacheConst.ORDER_LOCK_KEY,phone);
        RLock lock =  redissonClient.getLock(key);
        boolean res =redisUtil.lock(lock,key);
        if(res){
            try {

                QueryWrapper<BalanceRecord> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("order_no",orderNo);
                BalanceRecord balanceRecord =  balanceRecordService.getOne(queryWrapper);
                if(balanceRecord == null){
                    log.info("[{}] 退款[{}]订单不存在",Const.REQUEST_SERIAL_NUMBER.get(),orderNo);
                    return ResultDTO.factory(ResultCodeEnum.NO_ORDER);
                }
                if(0==balanceRecord.getStatus()){
                    log.info("[{}] 退款[{}]订单还未支付",Const.REQUEST_SERIAL_NUMBER.get(),orderNo);
                    return ResultDTO.factory(ResultCodeEnum.ORDER_NOT_PAY);
                }
                if(2==balanceRecord.getStatus()){
                    log.info("[{}] 退款[{}]订单已退过款",Const.REQUEST_SERIAL_NUMBER.get(),orderNo);
                    return ResultDTO.factory(ResultCodeEnum.ORDER_REFUND);
                }
                // 进行退款
                User user =userService.getById(balanceRecord.getCreateUser());
                Map<String,String> map ;
                if(user.getBalance().compareTo(balanceRecord.getAmount())>=0){
                    //用户账户余额大于或等于订单总额
                    user.setBalance(user.getBalance().subtract(balanceRecord.getAmount()));
                    map = this.refund(balanceRecord,balanceRecord.getAmount());
                }else{
                    // 账户余额少于订单总额,方案 1，直接失败，方案 2账户清0
                    map = this.refund(balanceRecord,user.getBalance());
                    user.setBalance(new BigDecimal("0"));
                }

                if("SUCCESS".equals(map.get("result_code"))){
                    balanceRecord.setStatus(2);
                    balanceRecordService.updateById(balanceRecord);
                    userService.updateById(user);
                    return new ResultDTO(true);
                }else{
                    log.info("[{}] 退款[{}] 退款失败[{}]",Const.REQUEST_SERIAL_NUMBER.get(),orderNo,map);
                    return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
                }

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(res){
                    lock.unlock();
                }
            }
        }else{
            log.info("[{}] 退款[{}]未获取到用户所",Const.REQUEST_SERIAL_NUMBER.get(),orderNo);
            return ResultDTO.factory(ResultCodeEnum.SERVER_BUSY);
        }

        return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
    }


    public ResultDTO getBalance(){
    User user = userService.getById(ConstSystem.currUser.get().getId());
        return new ResultDTO<>(true,user.getBalance());
    }

    public void bindOpenid(String openid){

        String key = String.format(CacheConst.USER_OPENID_KEY,ConstSystem.currUser.get().getPhone()) ;

        redisUtil.set(key,openid);

    }

    /**
     * 获取设备号
     * @param qrcode
     * @return
     */
    public ResultDTO getDeviceNo(String qrcode){
        QueryWrapper<BasicQrcode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("qrcode",qrcode);
        BasicQrcode  basicQrcode =  basicQrcodeService.getOne(queryWrapper);
        if(basicQrcode==null){
            return ResultDTO.factory(ResultCodeEnum.NO_PILE);
        }

        return new ResultDTO<>(true,basicQrcode.getDeviceNo(),"成功");
    }

}
