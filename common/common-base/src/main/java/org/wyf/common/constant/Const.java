package org.wyf.common.constant;

import java.util.List;

/**
 * 系统常量
 *
 * @author wangcw
 * @date 2017年2月12日 下午9:42:53
 */
public interface Const {



    public static ThreadLocal<ResultCodeEnum> UNAUTHORIZED = new ThreadLocal<>();

    /**
     * 当前选中 的 组织
     */
    public static ThreadLocal<Integer> orgId  =new ThreadLocal<>();

    public static ThreadLocal<List<Integer>> orgIds = new ThreadLocal<>();
    /**客户端类型*/
    ThreadLocal<String> CLIENT =  new ThreadLocal<>();
    /** 登录方式  */
    ThreadLocal<String> LOGIN_TYPE =  new ThreadLocal<>();


    /**
     *  当前令牌
     */
    public static ThreadLocal<String>  token   = new ThreadLocal<>();


    /**
     * 用户缓存
     * */
    public static final  String USER_KEY_PREFIX   = "tongyong:user";
    /**
     * 权限缓存
     */
    public static final  String PERMISSION_KEY_PREFIX = "tongyong:permission";


    public static final  ThreadLocal<String> REQUEST_SERIAL_NUMBER =new ThreadLocal<>();


}
