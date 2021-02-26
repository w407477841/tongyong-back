package org.wyf.cache;

public class CacheConst {


    public static String REDIS_HEADER = "tongyong";

    public static final String VALID_KEY =  REDIS_HEADER+":validate-code:";

    /**
     *  设备插头状态
     */
    public static String DEVICE_PLUS_STATUS = REDIS_HEADER+":"+"device-plus-status:%s:%s";
    /**
     *  订单锁 绑定手机号
     */
    public static String ORDER_LOCK_KEY = REDIS_HEADER+":"+"order-lock:%s";
    /**
     *  虚拟卡订单 ，绑定充电桩号
     */
    public static String ORDER_KEY = REDIS_HEADER+":"+"order:%s";


    /**
     *  订单号锁
     */
    public static String ORDER_NO_LOCK_KEY = REDIS_HEADER+":"+"order-no-lock";

    /**
     * 最后一条A8
     */
     public static String LAST_A8_KEY = REDIS_HEADER+":"+"last-a8:%s";

    /**
     * 插头锁
     */
    public static String PLUG_LOCK_KEY = REDIS_HEADER+":"+"plug-lock:%s:%s";

    /**  支付流程状态 存 60秒 */
    public static String MOBLIE_PAYMENT_STATUS_KEY = REDIS_HEADER+":"+"mobile-payment:status:%s:%s";


    /**
     * 订单总量
     */
    public static String ORDER_COUNT_KEY = REDIS_HEADER+":"+"order-count";

    /**
     * 卡与用户关系
     */
    public static String CARD_PHONE_KEY =  REDIS_HEADER+":"+"card-phone:%s";

    /**
     * netty端是否在线
     */
    public static String NETTY_ONLINE_KEY = REDIS_HEADER+":"+"netty-online";

    /**
     * 绑定 openid 用的
     */
    public static String USER_OPENID_KEY = REDIS_HEADER+":"+"user-openid:%s";


    /**
     * 微信 接口令牌
     */
    public static String ACCESS_TOKEN_KEY  = REDIS_HEADER+":"+"access-token";

    /**
     *  微信版本
     */
    public static String VERSION_KEY = REDIS_HEADER+":"+"version";


}
