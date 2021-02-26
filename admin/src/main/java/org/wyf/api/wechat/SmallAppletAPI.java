package org.wyf.api.wechat;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import org.wyf.aftersale.entity.AsIdea;
import org.wyf.aftersale.entity.AsMalfunction;
import org.wyf.balance.entity.BalanceRecord;
import org.wyf.balance.service.IBalanceRecordService;
import org.wyf.cache.CacheConst;
import org.wyf.cache.RedisUtil;
import org.wyf.cache.dto.DevicePlusStatusDTO;
import org.wyf.common.constant.Const;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.common.utils.MessageSendUtils;
import org.wyf.config.properties.WeChatProperties;
import org.wyf.order.entity.OrderRecord;
import org.wyf.service.SmallAppletService;
import org.wyf.system.ConstSystem;
import org.wyf.system.model.User;
import org.wyf.system.service.service.IUserService;
import org.wyf.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wyf.balance.entity.BalanceRecord;
import org.wyf.cache.CacheConst;
import org.wyf.cache.RedisUtil;
import org.wyf.cache.dto.DevicePlusStatusDTO;
import org.wyf.config.properties.WeChatProperties;
import org.wyf.service.SmallAppletService;
import org.wyf.system.ConstSystem;
import org.wyf.system.model.User;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/wechat/smallapplet")
@Slf4j
public class SmallAppletAPI {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedissonClient  redissonClient;

    @Autowired
    private IUserService userService;
    @Autowired
    private SmallAppletService smallAppletService;
    @Autowired
    private IBalanceRecordService balanceRecordService;
    @Autowired
    private WeChatProperties weChatProperties;

    private static final String CACHE_KEY =  "tongyong:validate-code:";

    private static final String SUCCESS = "<xml><return_code><![CDATA[SUCCESS]]></return_code> <return_msg><![CDATA[OK]]></return_msg></xml>";

    private static final String ERROR = "<xml><return_code><![CDATA[FAIL]]></return_code> <return_msg><![CDATA[OK]]></return_msg></xml>";

    private static final String NOTIFY_URL_PAY =  "https://xiaolv.v2x.net.cn:18090/wechat/smallapplet/payNotify";

    private static final String NOTIFY_URL_BALANCE =  "https://xiaolv.v2x.net.cn:18090/wechat/smallapplet/balanceNotify";

    public static final String NOTIFY_URL_REFUND =  "https://xiaolv.v2x.net.cn:18090/wechat/smallapplet/refundNotify";

    private static final String LOGIN_STR = "login";


    private static final String REGISTER_STR = "register";

    private static final String SUCCESS_STR = "SUCCESS";

    @PostMapping("validPhone")
    public ResultDTO validPhone(@RequestBody RequestDTO<RegisterDTO>  requestDTO){
        RegisterDTO user = requestDTO.getBody();
        if(user == null||StrUtil.isBlank(user.getPhone())){
            return  ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",user.getPhone());
        int count =   userService.count(queryWrapper);
        // 注册 手机号重复
        if(count>0){
            return  new ResultDTO(true,LOGIN_STR);
        }else{
            return  new ResultDTO(true,REGISTER_STR);
        }


    }

    /**
     * 发送验证码
     * 200  成功
     * 403  参数错误
     * 445  调用太频繁（之前的还没过期）
     * 2007 手机重复
     * 2013 验证码类型不存在
     * 2014 未注册
     * @return 标准返回体 
     */
    @PostMapping("sendValidateCode")
    public ResultDTO sendValidateCode(@RequestBody RequestDTO<RegisterDTO>  requestDTO){
        RegisterDTO user = requestDTO.getBody();
        if(user == null||StrUtil.isBlank(user.getPhone())||StrUtil.isBlank(user.getCodeType())){
            return  ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        if(!LOGIN_STR.equals(user.getCodeType())&&!REGISTER_STR.equals(user.getCodeType())){
            return  ResultDTO.factory(ResultCodeEnum.NO_VALIDCODE_TYPE);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",user.getPhone());
        int count =   userService.count(queryWrapper);
        // 注册 手机号重复
        if(count>0&&REGISTER_STR.equals(user.getCodeType())){
            return  ResultDTO.factory(ResultCodeEnum.PHONE_REPEAT);
        }
        // 登录 还未注册
        if(count<1&&LOGIN_STR.equals(user.getCodeType())){
            return  ResultDTO.factory(ResultCodeEnum.NO_REGISTER);
        }


        if(redisUtil.exists(CACHE_KEY+user.getCodeType()+":"+user.getPhone())){
            return  ResultDTO.factory(ResultCodeEnum.TOO_FREQUENT);
        }

        String code = RandomUtil.randomNumbers(6);
        redisUtil.set(CACHE_KEY+user.getCodeType()+":"+user.getPhone(),code,2L);
        if(REGISTER_STR.equals(user.getCodeType())){
            MessageSendUtils.registerValidCode(code,user.getPhone());
        }else{
            MessageSendUtils.loginValidCode(code,user.getPhone());
        }

        return ResultDTO.factory(ResultCodeEnum.SUCCESS);
    }


    /**
     *  注册账号，并绑定微信号
     * @param requestDTO 标准请求体
     * 200  成功
     * 403  参数错误
     * 2007 手机重复
     * 2009 验证码错误
     * @return 标准返回体
     */
    @PostMapping("register")
    public ResultDTO register(@RequestBody RequestDTO<RegisterDTO>  requestDTO){

         RegisterDTO user = requestDTO.getBody();
         if(user == null||StrUtil.isBlank(user.getPhone())||StrUtil.isBlank(user.getCode())){
             return  ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
         }
        if(!redisUtil.exists(CACHE_KEY+"register:"+user.getPhone())){
            return  ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        String code = (String) redisUtil.get(CACHE_KEY+"register:"+user.getPhone());
        if(!user.getCode().equals(code)){
            return  ResultDTO.factory(ResultCodeEnum.VALIDCODE_WRONG);
        }
        redisUtil.remove(CACHE_KEY+"register:"+user.getPhone());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",user.getPhone());
        int count =   userService.count(queryWrapper);
        if(count>0){
            return  ResultDTO.factory(ResultCodeEnum.PHONE_REPEAT);
        }
//        queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("wxname",user.getWxname());
//         count =   userService.count(queryWrapper);
//        if(count>1){
//            return  ResultDTO.factory(ResultCodeEnum.WXNAME_REPEAT);
//        }

        return smallAppletService.register(user);
    }

    /**
     * 登录
     * 200 成功
     * 403 参数错误
     * 2009 验证码错误
     * 2014 未注册
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("login")
    public ResultDTO login(@RequestBody RequestDTO<RegisterDTO>  requestDTO){

        RegisterDTO user = requestDTO.getBody();
        if(user == null||StrUtil.isBlank(user.getPhone())||StrUtil.isBlank(user.getCode())){
            return  ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }

        String code = (String) redisUtil.get(CACHE_KEY+"login:"+user.getPhone());
        if(!user.getCode().equals(code)){
            return  ResultDTO.factory(ResultCodeEnum.VALIDCODE_WRONG);
        }
        redisUtil.remove(CACHE_KEY+"login:"+user.getPhone());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",user.getPhone());
        int count =   userService.count(queryWrapper);
        if(count<1){
            return  ResultDTO.factory(ResultCodeEnum.NO_REGISTER);
        }

        return smallAppletService.login(user);
    }


    /**
     * 刷新 token(暂时不适用，怕葛妹适应不了)
     * 200 成功
     * 403  参数错误
     * 2010 Token验证失败
     * 2011 Token超时
     * 2012 Token失效
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("refreshToken")
    public ResultDTO refreshToken(@RequestBody RequestDTO<RegisterDTO> requestDTO){

        RegisterDTO user  = requestDTO.getBody();
        if(user ==null || StrUtil.isBlank(user.getRefreshToken())||StrUtil.isBlank(user.getPhone())){
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }

        return smallAppletService.refreshToken(user);
    }

    /**
     *
     * 绑定 虚拟卡
     * 200 成功
     * 403 参数错误
     * 4002 虚拟卡号不存在
     * 4003 该用户已绑定虚拟卡
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("bindCard")
    public ResultDTO bindCard(@RequestBody RequestDTO<BindCardDTO> requestDTO){

        BindCardDTO card  = requestDTO.getBody();
        if(card ==null || StrUtil.isBlank(card.getCardNo())){
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }

        return smallAppletService.bindCard(card);
    }

    /**
     *
     * 解绑 虚拟卡
     * 200 成功
     * 403 参数错误
     * 4002 虚拟卡号不存在
     * 4004 用户id不匹配
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("unbindCard")
    public ResultDTO unbindCard(@RequestBody RequestDTO<BindCardDTO> requestDTO){

        BindCardDTO card  = requestDTO.getBody();
        if(card ==null || StrUtil.isBlank(card.getCardNo())){
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }

        try {
            return smallAppletService.unbindCard(card);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
    }

    /**
     * 新增 意见反馈
     * 200 成功
     * 403 参数错误
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("insertAsIdea")
    public ResultDTO insertAsIdea(@RequestBody RequestDTO<AsIdea> requestDTO){
        try {
            //参数判空
            AsIdea asIdea  = requestDTO.getBody();
            if(asIdea == null || StrUtil.isBlank(asIdea.getIdeaContent())){
                return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
            }
            return  smallAppletService.insertAsIdea(requestDTO.getBody());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
    }

    /**
     * 查询 意见反馈
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("selectAsIdea")
    public ResultDTO selectAsIdea(@RequestBody RequestDTO<AsIdea> requestDTO){
        if(requestDTO.getPageSize() == null || requestDTO.getPageNum() == null){
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        return smallAppletService.selectAsIdea(requestDTO);
    }

    /**
     * 更新 意见反馈解决状态
     * 200 成功
     * 403 参数错误
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("updateAsIdea")
    public ResultDTO updateAsIdea(@RequestBody RequestDTO<AsIdea> requestDTO){
        //参数判空
        AsIdea asIdea  = requestDTO.getBody();
        if(asIdea == null || asIdea.getId() == null || asIdea.getFeedbackStatus() == null){
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        return smallAppletService.updateAsIdea(requestDTO);
    }

    /**
     * 新增 故障报修
     * 200 成功
     * 403 参数错误
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("insertAsMalfunction")
    public ResultDTO insertAsMalfunction(@RequestBody RequestDTO<AsMalfunctionVO> requestDTO){
        try {
            //参数判空
            AsMalfunctionVO asMalfunction  = requestDTO.getBody();
            if(asMalfunction == null || StrUtil.isBlank(asMalfunction.getDeviceNo())
                || StrUtil.isBlank(asMalfunction.getPlugNo()) || StrUtil.isBlank(asMalfunction.getReportTime())
                || StrUtil.isBlank(asMalfunction.getMalfunctionContent())){
                return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
            }
            return  smallAppletService.insertAsMalfunction(requestDTO.getBody());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
    }

    /**
     * 查询 故障报修
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("selectAsMalfunction")
    public ResultDTO selectAsMalfunction(@RequestBody RequestDTO<AsMalfunction> requestDTO){
        if(requestDTO.getPageSize() == null || requestDTO.getPageNum() == null){
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        return smallAppletService.selectAsMalfunction(requestDTO);
    }

    /**
     * 更新 故障报修解决状态
     * 200 成功
     * 403 参数错误
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("updateAsMalfunction")
    public ResultDTO updateAsMalfunction(@RequestBody RequestDTO<AsMalfunction> requestDTO){
        //参数判空
        AsMalfunction asMalfunction  = requestDTO.getBody();
        if(asMalfunction == null || asMalfunction.getId() == null || asMalfunction.getFeedbackStatus() == null){
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        return smallAppletService.updateAsMalfunction(requestDTO);
    }

    /**
     * 充电桩 ，
     * 200 成功
     * 403 参数错误
     * 3003 充电桩不存在
     * @return 标准返回体
     */
    @PostMapping("getPileStatus")
    public ResultDTO getPileStatus(@RequestBody RequestDTO<MobilePaymentChargingDTO> requestDTO){
        MobilePaymentChargingDTO dto = requestDTO.getBody();
        if (dto == null || StrUtil.isBlank(dto.getDeviceNo())) {
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        return smallAppletService.getPileStatus(dto.getDeviceNo());
    }

    /**
     * 获取订单号
     * 200 成功
     * 403 参数错误
     * 3002 插头被占用
     * 3003 充电桩不存在
     * 3005 插头故障
     * 3004 充电桩不在线
     * 5000 服务器繁忙
     *
     * */
    @PostMapping("getOrderNo")
    public ResultDTO getOrderNo(@RequestBody RequestDTO<MobilePaymentChargingDTO> requestDTO) {
        MobilePaymentChargingDTO dto = requestDTO.getBody();
        if (dto == null || StrUtil.isBlank(dto.getDeviceNo()) || StrUtil.isBlank(dto.getPlug()) || dto.getAmount()==null ||StrUtil.isBlank(dto.getOpenid())) {
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        ResultDTO validDevice = smallAppletService.validDevice(dto.getDeviceNo());
        if(null!=validDevice){
            return validDevice;
        }
        smallAppletService.bindOpenid(dto.getOpenid());
        // 生成订单号
        String orderNo = smallAppletService.getOrderNO();
        if (null == orderNo) {
            log.info("订单号获取失败");
            return ResultDTO.factory(ResultCodeEnum.SERVER_BUSY);
        }
        if (!redisUtil.exists(CacheConst.NETTY_ONLINE_KEY)) {
            log.info("A7指令调用失败[Netty服务器不在线]");
            ResultDTO.factory(ResultCodeEnum.SERVER_BUSY);
        }

        String key = String.format(CacheConst.PLUG_LOCK_KEY, dto.getDeviceNo(), dto.getPlug());
        // 插头锁 ，禁止同时操作插头

        RLock lock = redissonClient.getLock(key);
        boolean res = false;
        try {
            // 等待 5秒，60秒后自动解锁 防止死锁。
            res = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (res) {
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


                // 修改状态
                devicePlusStatusDTO.setOrderNo(orderNo);
                //   devicePlusStatusDTO.setStatus(1);
                redisUtil.set(String.format(CacheConst.DEVICE_PLUS_STATUS, dto.getDeviceNo(), dto.getPlug()), JSONUtil.toJsonStr(devicePlusStatusDTO));

                // 统一下单
               Map<String,String> map = smallAppletService.unifiedorder(orderNo,dto.getAmount(),dto.getOpenid(),NOTIFY_URL_PAY);
               if("SUCCESS".equals(map.get("result_code"))){
                    Map<String,String> data = new HashMap<>(32);
                    data.put("timeStamp",WXPayUtil.getCurrentTimestamp()+"");
                    data.put("nonceStr",map.get("nonce_str"));
                    data.put("package","prepay_id="+map.get("prepay_id"));
                    data.put("appId",weChatProperties.getAppid());
                   String sign ;
                   if(weChatProperties.isUseSandbox()){
                        data.put("signType",WXPayConstants.MD5);
                       sign= WXPayUtil.generateSignature(data,weChatProperties.getAppkey());
                    }else{
                        data.put("signType",WXPayConstants.HMACSHA256);
                       sign= WXPayUtil.generateSignature(data,weChatProperties.getAppkey(),WXPayConstants.SignType.HMACSHA256);
                    }

                    data.put("paySign",sign);
                    // 生成订单
                    dto.setOrderNo(orderNo);
                    smallAppletService.wxPayment(dto,map.get("prepay_id"));

                    UnifiedorderVO unifiedorderVO = new UnifiedorderVO();
                    unifiedorderVO.setOrderNo(orderNo);
                   unifiedorderVO.setPayData(data);
                   return new ResultDTO<>(true, unifiedorderVO, "成功");
               }else{
                   log.info("[{}] 统一下单失败[{}]",Const.REQUEST_SERIAL_NUMBER.get(),map);
                   return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
               }
            } else {
                return ResultDTO.factory(ResultCodeEnum.SERVER_BUSY);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
        } finally {
            if (res) {
                lock.unlock();
            }
        }
    }
    /**
     * 手机支付充电
     * 200 成功
     * 403 参数错误
     * 3003 充电桩不存在
     * 3004 充电桩不在线
     * 4000  订单号【】开启失败，请联系客服
     * @return 标准返回体
     */
    @PostMapping("mobilePaymentCharging")
    public ResultDTO mobilePaymentCharging(@RequestBody RequestDTO<MobilePaymentChargingDTO> requestDTO) {

        MobilePaymentChargingDTO dto = requestDTO.getBody();
        if (dto == null || StrUtil.isBlank(dto.getDeviceNo()) || StrUtil.isBlank(dto.getOrderNo()) || StrUtil.isBlank(dto.getPlug()) || null == dto.getAmount()) {
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        ResultDTO validDevice = smallAppletService.validDevice(dto.getDeviceNo());
        if(null!=validDevice){
            return validDevice;
        }


        if(smallAppletService.exist(dto.getOrderNo())){
            return ResultDTO.factory(ResultCodeEnum.ORDER_EXECUTED);
        }
        // 修改订单
        return  smallAppletService.udpWxPayment(dto);


    }

    /**
     * 异常支付 ,未支付/ 支付失败
     * 200 成功
     * 403 参数错误
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("errorPayment")
    public ResultDTO errorPayment(@RequestBody RequestDTO<MobilePaymentChargingDTO> requestDTO){
        MobilePaymentChargingDTO dto = requestDTO.getBody();
        if (dto == null || StrUtil.isBlank(dto.getDeviceNo())  || StrUtil.isBlank(dto.getPlug())) {
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }


        redisUtil.remove(String.format(CacheConst.MOBLIE_PAYMENT_STATUS_KEY,dto.getDeviceNo(), dto.getPlug()));

        return ResultDTO.success("成功");


    }


    /**
     * 余额充值
     * 200 成功
     * 403 参数错误
     * 5000 服务器繁忙
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("getBalanceOrderNo")
    public ResultDTO getBalanceOrderNo(@RequestBody RequestDTO<BalanceDTO> requestDTO){
        BalanceDTO dto = requestDTO.getBody();
        if (dto == null  || null == dto.getAmount()|| StrUtil.isBlank(dto.getOpenid())) {
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        smallAppletService.bindOpenid(dto.getOpenid());
        String orderNo = smallAppletService.getOrderNO();
        if (null == orderNo) {
            log.info("[{}] 订单号获取失败",Const.REQUEST_SERIAL_NUMBER.get());
            return ResultDTO.factory(ResultCodeEnum.SERVER_BUSY);
        }
        String phone  = ConstSystem.currUser.get().getPhone() ;
        dto.setPhone(phone);
        String key = String.format(CacheConst.ORDER_LOCK_KEY,dto.getPhone());
        RLock lock =  redissonClient.getLock(key);
        boolean res =redisUtil.lock(lock,key);
        try {

            if(res) {
                // 统一下单
                Map<String, String> map = smallAppletService.unifiedorder(orderNo, dto.getAmount(), dto.getOpenid(),NOTIFY_URL_BALANCE);
                if (SUCCESS_STR.equals(map.get("result_code"))) {
                    Map<String, String> data = new HashMap<>(32);
                    data.put("timeStamp", WXPayUtil.getCurrentTimestamp() + "");
                    data.put("nonceStr", map.get("nonce_str"));
                    data.put("package", "prepay_id=" + map.get("prepay_id"));
                    data.put("appId", weChatProperties.getAppid());
                    String sign ;
                    if(weChatProperties.isUseSandbox()){
                        data.put("signType",WXPayConstants.MD5);
                        sign= WXPayUtil.generateSignature(data,weChatProperties.getAppkey());
                    }else{
                        data.put("signType",WXPayConstants.HMACSHA256);
                        sign= WXPayUtil.generateSignature(data,weChatProperties.getAppkey(),WXPayConstants.SignType.HMACSHA256);
                    }
                    data.put("paySign", sign);

                    // 生成订单
                    dto.setOrderNo(orderNo);
                    BalanceRecord balanceRecord = new BalanceRecord();
                    balanceRecord.setAmount(dto.getAmount());
                    balanceRecord.setPhone(dto.getPhone());
                    balanceRecord.setOrderNo(orderNo);
                    balanceRecord.setStatus(0);
                    balanceRecordService.save(balanceRecord);

                    UnifiedorderVO unifiedorderVO = new UnifiedorderVO();
                    unifiedorderVO.setOrderNo(orderNo);
                    unifiedorderVO.setPayData(data);
                    return new ResultDTO<>(true, unifiedorderVO, "成功");
                } else {
                    log.info("[{}] 统一下单失败[{}]",Const.REQUEST_SERIAL_NUMBER.get(), map);
                    return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
                }
            }else{
                log.info("[{}] 获取锁失败[{}]",Const.REQUEST_SERIAL_NUMBER.get(), orderNo);
                return ResultDTO.factory(ResultCodeEnum.SERVER_BUSY);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
        }finally {
            if(res){
                lock.unlock();
            }
        }
    }

    /**
     * 余额充值
     * 200 成功
     * 403 参数错误
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("addBalance")
    public ResultDTO addBalance(@RequestBody RequestDTO<BalanceDTO> requestDTO){
        BalanceDTO dto = requestDTO.getBody();
        if (dto == null || StrUtil.isBlank(dto.getPhone())  || null == dto.getAmount() || StrUtil.isBlank(dto.getOrderNo())) {
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        return smallAppletService.addBalance(dto);
    }

    /**
     * 查询余额
     * 200 成功
     * @return 标准返回体
     */
    @PostMapping("getBalance")
    public ResultDTO getBalance(){



        return smallAppletService.getBalance();
    }
    /**
     * 余额支付
     * 200 成功
     * @return 标准返回体
     */
    @PostMapping("payWithBalance")
    public ResultDTO payWithBalance(@RequestBody RequestDTO<MobilePaymentChargingDTO> requestDTO){
        MobilePaymentChargingDTO dto = requestDTO.getBody();
        if (dto == null || StrUtil.isBlank(dto.getDeviceNo())  || StrUtil.isBlank(dto.getPlug()) || null == dto.getAmount() || StrUtil.isBlank(dto.getOpenid())) {
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        smallAppletService.bindOpenid(dto.getOpenid());
        ResultDTO validDevice = smallAppletService.validDevice(dto.getDeviceNo());
        if(null!=validDevice){
            return validDevice;
        }

        return smallAppletService.payWithBalance(dto);
    }

    /**
     *
     * 换取用户openid
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("jscode2session")
    public ResultDTO jscode2session(@RequestBody RequestDTO<WeChatDTO> requestDTO){
        WeChatDTO dto = requestDTO.getBody();
        if (dto == null || StrUtil.isBlank(dto.getCode())  ) {
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }

         String result = smallAppletService.jscode2session(dto.getCode());
        return new ResultDTO<>(true,JSONUtil.toBean(result,Jscode2sessionVO.class));
    }



    /**
     *
     * 支付回调
     *
     * @return 标准返回体
     */
    @PostMapping(value = "/payNotify", produces = {"application/xml;charset=UTF-8"})
    public String payNotify(HttpServletRequest request){

        log.info("微信支付回调[{}]",request);
        String resultxml;
        try {
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len ;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            resultxml = new String(outSteam.toByteArray(), "utf-8");
            Map<String, String> map = WXPayUtil.xmlToMap(resultxml);
            log.info("微信支付回调[{}]", map.toString());

            MobilePaymentChargingDTO dto = new MobilePaymentChargingDTO();
            dto.setOrderNo(map.get("out_trade_no"));
            dto.setAmount(new BigDecimal(map.get("total_fee")).divide(new BigDecimal("100"), BigDecimal.ROUND_HALF_UP));
            // 生成订单

            return smallAppletService.udpWxPaymentStr(dto);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }



    /**
     *
     * 充值回调
     *
     * @return 标准返回体
     */
    @PostMapping(value = "/balanceNotify", produces = {"application/xml;charset=UTF-8"})
    public String balanceNotify(HttpServletRequest request){

        log.info("微信支付回调[{}]",request);
        String resultxml;
        try {
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            resultxml = new String(outSteam.toByteArray(), "utf-8");
            Map<String, String> map = WXPayUtil.xmlToMap(resultxml);
            log.info("微信支付回调[{}]", map.toString());
            String orderNo = map.get("out_trade_no");
            // 生成订单
            QueryWrapper<BalanceRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_no",orderNo);
            BalanceRecord balanceRecord =  balanceRecordService.getOne(queryWrapper);
            if(balanceRecord == null){
                log.info("余额充值[{}]订单不存在",orderNo);
                return SUCCESS;
            }
            if(1==balanceRecord.getStatus()){
                log.info("余额充值[{}]订单已完成",orderNo);
                return SUCCESS;
            }
            String key = String.format(CacheConst.ORDER_LOCK_KEY,balanceRecord.getPhone());
            RLock lock =  redissonClient.getLock(key);
            boolean res =redisUtil.lock(lock,key);
            try {

                if(res){


                    QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                    userQueryWrapper.eq("phone",balanceRecord.getPhone());
                    User user =  userService.getOne(userQueryWrapper);
                    if(null == user){
                        log.info("余额充值[{}]手机号不存在",balanceRecord.getPhone());
                        return SUCCESS;
                    }
                    balanceRecord.setStatus(1);
                    BigDecimal amount =  balanceRecord.getAmount();

                    if(user.getBalance()==null){
                        user.setBalance(amount);
                    }else{
                        user.setBalance(amount.add(user.getBalance()));
                    }

                    balanceRecordService.updateById(balanceRecord);
                    userService.updateById(user);
                    return SUCCESS;
                }else{
                    log.info("余额充值[{}]未拿到用户锁",balanceRecord.getPhone());
                    return ERROR;
                }

            }finally {
                if(res){
                    lock.unlock();
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    /**
     *
     * 获取订单详情
     * 200 成功
     * 403 参数错误
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("getOrderRecord")
    public ResultDTO getOrderRecord(@RequestBody RequestDTO<OrderRecord> requestDTO){

        if(requestDTO.getPageSize() == null || requestDTO.getPageNum() == null){
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        return smallAppletService.getOrderRecord(requestDTO);
    }

    /**
     *
     * 获取卡列表
     * 200 成功
     * @return 标准返回体
     */
    @PostMapping("getCardno")
    public ResultDTO getCardno(){
        return smallAppletService.getCardno();
    }

    /**
     *
     * 获取设备号
     * 200 成功
     * 403 参数错误
     * 3003 充电桩不存在
     * @param requestDTO 标准请求体
     * @return 标准返回体
     */
    @PostMapping("getDeviceNo")
    public ResultDTO getDeviceNo(@RequestBody RequestDTO<String> requestDTO){
        String qrcode = requestDTO.getBody();
        if(StrUtil.isBlank(qrcode)){
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }

        return smallAppletService.getDeviceNo(qrcode);
    }

    /**
     * 退款
     * 200 成功
     * @param requestDTO
     * @return
     */
    @PostMapping("refund")
    public ResultDTO refund(@RequestBody RequestDTO<String> requestDTO){
        String orderNo = requestDTO.getBody();
        if(StrUtil.isBlank(orderNo)){
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        return smallAppletService.refund(orderNo);

    }

    /**
     *
     * @return
     */
    @PostMapping("getVersion")
    public ResultDTO getVersion(){

        if(redisUtil.exists(CacheConst.VERSION_KEY)){
            return new ResultDTO(true,(String)redisUtil.get(CacheConst.VERSION_KEY));

        }else{

            return new ResultDTO(true,"0");
        }


    }


    @PostMapping("updVersion")
    public ResultDTO updVersion(@RequestBody RequestDTO<String> requestDTO){
        String version = requestDTO.getBody();
        if(StrUtil.isBlank(version)){
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        redisUtil.set(CacheConst.VERSION_KEY,version);
            return new ResultDTO(true,"成功");


    }

}
