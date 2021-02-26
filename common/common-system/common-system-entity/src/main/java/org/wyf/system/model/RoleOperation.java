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
@TableName("t_sys_role_operation")
@Data
public class RoleOperation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 角色id
     */
    @TableField("role_id")
    private Integer roleId;
    /**
     * 所属部门
     */
    @TableField("oper_id")
    private Integer operId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String comments;




}
