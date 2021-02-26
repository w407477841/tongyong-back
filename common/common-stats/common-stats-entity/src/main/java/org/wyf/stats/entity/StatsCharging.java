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
@TableName("t_stats_charging")
public class StatsCharging extends BaseEntity {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 站点(方便根据站点统计)
     */
    private Integer stationId;

    /**
     * 物业(方便根据物业统计)
     */
    private Integer orgId;

    /**
     * 充电金额
     */
    private BigDecimal amount;

    /**
     * 00-02金额
     */
    private BigDecimal amount0002;

    /**
     * 02-04金额
     */
    private BigDecimal amount0204;

    /**
     * 04-06金额
     */
    private BigDecimal amount0406;

    /**
     * 06-08金额
     */
    private BigDecimal amount0608;

    /**
     * 08-10金额
     */
    private BigDecimal amount0810;

    /**
     * 10-12金额
     */
    private BigDecimal amount1012;

    /**
     * 12-14金额
     */
    private BigDecimal amount1214;

    /**
     * 14-16金额
     */
    private BigDecimal amount1416;

    /**
     * 16-18金额
     */
    private BigDecimal amount1618;

    /**
     * 18-20金额
     */
    private BigDecimal amount1820;

    /**
     * 20-22金额
     */
    private BigDecimal amount2022;

    /**
     * 22-24金额
     */
    private BigDecimal amount2224;

    /**
     * 占用分钟数
     */
    private Integer holdingMins;

    /**
     * 占用率
     */
    private BigDecimal holdingRate;

    /**
     * 空闲分钟数
     */
    private Integer freeMins;

    /**
     * 统计日期
     */
    private String statisticsDay;

    /**
     * 备注
     */
    private String comments;


}
