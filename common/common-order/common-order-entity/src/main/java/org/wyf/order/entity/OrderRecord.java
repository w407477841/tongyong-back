package org.wyf.order.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.wyf.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.wyf.common.entity.BaseEntity;

/**
 * <p>
 * 
 * </p>
 *
 * @author wyf
 * @since 2019-11-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order_record")
public class OrderRecord extends BaseEntity {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 系统订单编号
     */
    private String orderNo;

    /**
     * 微信订单号
     */
    private String wxOrderNo;

    /**
     * 设备号
     */
    private String deviceNo;

    /**
     * 卡号
     */
    private  String cardNo;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 插头号
     */
    private String plugNo;

    /**
     * 订单标记(1 ：微信支付，2刷卡支付)
     */
    private Integer orderTag;

    /**
     * 单价元/小时
     */
    private BigDecimal price;

    /**
     * 小时数
     */
    private Integer hourage;

    /**
     * 合计
     */
    private BigDecimal amount;

    /**
     * 订单状态（0取消支付，1支付成功，2支付失败）
     */
    private Integer status;

    /**
     * 指令状态(0:未下发，1:已下发，2：成功，3：失败)
     */
    private Integer cmdStatus;

    /**
     * 备注
     */
    private String comments;

    /**
     * 支付时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date payTime;

    /**
     * 指令下发时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date cmdSendTime;

    /**
     * 指令回复时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date cmdReplyTime;
    /**
     * 充电开始时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date chargingStartTime;
    /**
     * 充电结束时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date chargingEndTime;

    private Integer chargingStatus;

}
