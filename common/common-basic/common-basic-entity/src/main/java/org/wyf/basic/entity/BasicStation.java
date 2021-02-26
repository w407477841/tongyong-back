package org.wyf.basic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @author wyf
 * @since 2019-11-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_basic_station")
public class BasicStation extends BaseEntity {


    /**
     *  主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 站点名称
     */
    private String name;

    /**
     * 地址
     */
    private String address;

    /**
     * 坐标
     */
    private String coordinate;

    /**
     * 备注
     */
    private String comments;

    /**
     * 物业
     */
    private Integer orgId;

    /**
     * 物业名称
     */
    @TableField(exist = false)
    private String orgName;
}
