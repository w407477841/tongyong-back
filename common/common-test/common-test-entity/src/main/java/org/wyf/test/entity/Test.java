package org.wyf.test.entity;

import com.baomidou.mybatisplus.annotation.*;
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
 * @since 2019-11-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_test")
public class Test extends BaseEntity {


    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 组织
     */
    @TableField(value="org_id", fill = FieldFill.INSERT)
    private Integer orgId;

    /**
     * 备注
     */
    private String comments;


}
