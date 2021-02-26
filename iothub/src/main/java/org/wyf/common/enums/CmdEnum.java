package org.wyf.common.enums;

import cn.hutool.core.util.StrUtil;

/**
 *
 */
public enum CmdEnum {
    A4("A4","设置电流标准反馈"),
    A7("A7","网络通道充电任务反馈"),
    A8("A8","虚拟卡上报预消费信息"),
    B1("B1","设置充满等待断电时长反馈"),
    B2("B2","查询充满等待断电时长反馈"),
    B3("B3","查询设备实时电流反馈"),
    B4("B4","本地修改消费标准"),
    B5("B5","主动上报投币订单信息"),
    B6("B6","主动上报刷卡订单信息"),
    B7("B7","设置消费标准反馈"),
    B8("B8","查询消费标准反馈"),
    C1("C1","查询投币消费总金额反馈"),
    C2("C2","查询刷卡消费总金额反馈"),
    C3("C3","查询电流标准反馈"),
    C4("C4","上报设备状态"),
    C8("C8","虚拟卡工作完成上报订单信息"),
    C9("C9","网络任务完成上报消费订单"),
    D1("D1","设置浮充充电站功率值反馈"),
    D2("D2","查询浮充充电站功率值反馈"),
    D4("D4","查询设备插头状态反馈"),
    D6("D6","清除通道时间反馈"),
    D7("D7","设备重启反馈"),
    D8("D8","设置充满断电开关反馈"),
    E1("E1","设置充电站功率AD值"),
    E2("E2","查询充电站功率AD值"),
    E3("E3","设置空载断电功率值反馈"),
    E4("E4","查询空载断电功率值反馈"),
    E5("E5","设置空载断电等待时长反馈"),
    E6("E6","查询空载断电等待时长反馈"),
    E7("E7","更新设备时间"),
    F4("F4","查询版本号反馈"),
    F5("F5","恢复出厂设置反馈"),
    ;

    private String cmd;

    private String logfmt;

    public String getCmd() {
        return cmd;
    }

    public String getLogfmt() {
        return logfmt;
    }

    CmdEnum(String cmd, String logfmt) {
        this.cmd = cmd;
        this.logfmt = logfmt;
    }
    
    
    public static CmdEnum getEnum(String cmd){
         if(StrUtil.isBlank(cmd)){
             return null;
         }
        for (CmdEnum cmdEnum : CmdEnum.values()) {
            if(cmd.equals(cmdEnum.cmd)){
                return cmdEnum;
            }
        }
        return null;
    }
    
}
