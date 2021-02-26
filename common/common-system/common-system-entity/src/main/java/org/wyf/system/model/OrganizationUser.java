package org.wyf.system.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author wyf123
 * @since 2018-08-13
 */
@TableName("t_sys_organization_user")
@Data
public class OrganizationUser implements Serializable {

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
     * 所属部门
     */
    @TableField(value = "org_id")
    private Integer orgId;
    /**
     * 所属集团
     */
    @TableField("group_id")
    private Integer groupId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String comments;
    /**
     * 
     */
    @TableLogic
    @TableField(value="is_del",fill=FieldFill.INSERT)
    private Integer isDel;
    /**
     * 创建日期
     */
    @TableField(value="create_time",fill=FieldFill.INSERT)
    private Date createTime;
    /**
     * 创建人
     */
    @TableField(value="create_user",fill=FieldFill.INSERT)
    private Integer createUser;
    /**
     * 修改日期
     */
    @TableField(value="modify_time",fill=FieldFill.UPDATE)
    private Date modifyTime;
    /**
     * 修改人
     */
    @TableField(value="modify_user",fill=FieldFill.UPDATE)
    private Integer modifyUser;




}
