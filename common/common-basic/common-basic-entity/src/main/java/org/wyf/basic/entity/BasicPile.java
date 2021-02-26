package org.wyf.basic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.wyf.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.wyf.common.entity.BaseEntity;

import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author wyf
 * @since 2019-11-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_basic_pile")
public class BasicPile extends BaseEntity {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属站点
     */
    private Integer stationId;

    /**
     * 站名
     */
    @TableField(exist = false)
    private String stationName;

    /**
     * 桩名称
     */
    private String name;

    /**
     * 设备号
     */
    private String deviceNo;

    /**
     * 单价元/小时
     */
    private BigDecimal price;

    /**
     * 状态(0正常，1报警，2维修中)
     */
    private Integer status;

    /**
     * 在线(1在线，0离线)
     */
    private Integer online;

    /**
     * 备注
     */
    private String comments;


}
