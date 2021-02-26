package org.wyf.system.model;

import com.baomidou.mybatisplus.annotation.*;
import org.wyf.common.entity.BaseEntity;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author wyf123
 * @since 2018-08-13
 */
@TableName("t_sys_user_role")
@Data
public class UserRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;
    /**
     * 角色id
     */
    @TableField("role_id")
    private Integer roleId;
    /**
     * 所属组织
     */
    @TableField(value = "org_id",fill = FieldFill.INSERT)
    private Integer orgId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String comments;

}
