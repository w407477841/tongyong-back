package org.wyf.stats.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @author wangyifei
 * @since 2020-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_stats_recharge")
public class StatsRecharge extends BaseEntity {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 充值金额
     */
    private BigDecimal amount;

    /**
     * 30充值次数
     */
    private Integer frequency30;

    /**
     * 50充值次数
     */
    private Integer frequency50;

    /**
     * 100充值次数
     */
    private Integer frequency100;

    /**
     * 150充值次数
     */
    private Integer frequency150;


    /**
     * 200充值次数
     */
    private Integer frequency200;

    /**
     * 300充值次数
     */
    private Integer frequency300;

    /**
     * 统计日期 yyyy-MM-dd
     */
    private String statisticsDay;

    /**
     * 备注
     */
    private String comments;


}
