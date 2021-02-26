package org.wyf.system.model;

import com.baomidou.mybatisplus.annotation.*;
import org.wyf.common.entity.BaseEntity;
import lombok.Data;


/**
 * <p>
 * 
 * </p>
 *
 * @author wyf
 * @since 2018-08-16
 */
@TableName("t_sys_role")
@Data
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 名称
     */
	private String name;
    /**
     * 编码
     */
	private String code;
    /**
     * 描述
     */
	private String instroction;
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
