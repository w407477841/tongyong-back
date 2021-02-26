package org.wyf.balance.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import org.wyf.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wyf
 * @since 2019-11-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_balance_record")
public class BalanceRecord extends BaseEntity {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 订单状态（1：已支付，0：未支付）
     */
    private Integer status;

    /**
     * 备注
     */
    private String comments;


}
