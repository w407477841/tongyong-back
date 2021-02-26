package org.wyf.common.constant;

/**
 * 
 * @date 2018年8月2日
 * @company jsxywg
 */
public enum ResultCodeEnum {
    /** 成功 */
    SUCCESS(200, "操作成功"),
    
    /** 没有登录 */
    FAIL(300, "操作失败"),

    /** 没有登录 */
    NOT_LOGIN(400, "请重新登录"),
    TOEKN_ERROR(400, "TOKEN异常"),
    /** 发生异常 */
    EXCEPTION(401, "发生异常"),

    /** 系统错误 */
    SYS_ERROR(402, "系统错误"),

    /** 参数错误 */
    PARAMS_ERROR(403, "参数错误 "),
    /** 密码错误*/
    UNAUTHORIZED(404, "密码错误 "),
    /**用户名错误或用户名不存在*/
    NOUSER(405, "用户名错误或用户名不存在 "),
    NO_VALID_CODE(405, "先获取验证码 "),
    VALID_CODE_ERROR(405, "验证码错误 "),
    /** 未定义请求 */
    NOT_FOUND(406, "请求资源不存在"),

    /** 不支持或已经废弃 */
    NOT_SUPPORTED(410, "不支持或已经废弃"),

    /** AuthCode错误 */
    INVALID_AUTH_CODE(444, "无效的AuthCode"),

    /** 太频繁的调用 */
    TOO_FREQUENT(445, "太频繁的调用"),

    /** 未知的错误 */
    UNKNOWN_ERROR(499, "未知错误"),
    
    /** 无权限 */
    NO_PERMISSION(1999, "暂未开通此功能"),
    
    /** 录入的用户手机号或者登陆名已经存在*/
    HAS_USER(1998, "手机号或者登陆名已经存在"),
    
    /** 用户不在任何公司*/
    NO_ORG(1997, "用户没有加入任何公司"),

    
    /** 不能删除主公司*/
    NO_DELORG(1996, "不能删除主公司"),
    
    /** 不能删除管理员*/
    NO_DELMANAGGER(1995, "不能删除管理员"),
    
    /** 不能删除管理员*/
    HAS_PERMISSION(1994, "已经存在该权限字符"),
    
    /** 不能删除管理员*/
    NO_DATASHOW(1993, "没有浏览权限"),
    
    /** 被踢出下线*/
    HAS_KICK_OUT(1992, "被迫下线,请重新登录"),

    /**需要选择组织*/
    SELECT_ORG(1890,"请选择一个组织"),


    /** 数据字典设备类型名称重复*/
    DICT_SERVICE_TYPE_NAME_REPEAT(2001, "名称重复"),

    /** 数据字典设备类型名称为空*/
    DICT_SERVICE_TYPE_NAME_BLANK(2002, "数据字典设备类型名称为空"),

    PROJECT_NAME_REPEAT(2003,"工程名称重复"),

    LOGIN_NAME_REPEAT(2005,"登录名重复"),

    PASSWORD_WRONG(2006,"原密码错误"),

    PHONE_REPEAT(2007,"手机号重复"),

    WXNAME_REPEAT(2008,"微信号重复"),

    VALIDCODE_WRONG(2009,"验证码错误"),

    TOKEN_WRONG(2010,"Token验证失败"),
    TOKEN_TIMEOUT(2011,"Token超时"),
    INVALID_TOKEN(2012,"Token失效"),
    NO_VALIDCODE_TYPE(2013,"验证码类型不存在"),
    NO_REGISTER(2014,"未注册"),
    VALIDATECODE_TOKEN(2015,"验证码失效"),

    QRCODE_EXIST(2016,"二维码重复"),

    DEVICE_BINDED_QRCODE(2017,"设备已绑定过二维码"),

    PILE_EXIST(2018,"所属站点的桩名重复"),

    DEVICE_BINDED_PILE(2019,"设备已绑定过桩"),
    // 远控类

    REMOTE_SERVER_OFFLINE(3001,"服务不在线"),
    REMOTE_PLUG_OCCUPIED(3002,"插头被占用"),
    REMOTE_PLUG_ERROR(3005,"插头故障"),

    NO_PILE(3003,"充电桩不存在"),
    PILE_OFFLINE(3004,"充电桩不在线"),
    ORDER_EXECUTED(3006,"订单已存在"),

    NOT_ENOUGH_BALANCE(3007,"余额不足"),


    NO_ORDER(3008,"订单不存在"),

    ORDER_NOT_PAY(3009,"订单未支付"),

    ORDER_REFUND(3010,"订单已退款"),

    // 微信
    /**微信预处理失败*/
    WX_PRETREATMENT_FAILED(4001,"微信预处理失败"),

    /** card_no不存在 */
    NO_CARDNO(4002,"虚拟卡号不存在"),



    /** 用户id已绑定虚拟卡 */
    USERID_BINDED(4003,"虚拟卡已被用户绑定"),

    /** 用户id与库里不匹配 */
    NO_WATCH_USERID(4004,"用户id不匹配"),

    CARDNO_EXIST(4005,"虚拟卡号已存在"),

    // 终极大招
    SERVER_BUSY(5000,"服务器繁忙"),

    ;

     ResultCodeEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer code() {
        return code;
    }

    public String msg() {
        return msg;
    }

    private Integer code;
    private String msg;
}
