package org.wyf.system.model;

import com.baomidou.mybatisplus.annotation.*;
import org.wyf.common.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;


/**
 * <p>
 * 
 * </p>
 *
 * @author wyf123
 * @since 2018-08-13
 */
@TableName("t_sys_user")
@Data
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 所属部门
     */
    @TableField(value = "org_id",fill = FieldFill.INSERT)
    private Integer orgId;
    /**
     * 用户编码
     */
    private String code;
    /**
     * 用户手机
     */
    private String phone;
    /**
     * 用户名称
     */
    private String name;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 来源标记
     */
    private Integer flag;


    private String wxname;


    private BigDecimal balance;

    /**
     * 关联用户id
     */
    @TableField("relation_user_id")
    private Integer relationUserId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String comments;





}
