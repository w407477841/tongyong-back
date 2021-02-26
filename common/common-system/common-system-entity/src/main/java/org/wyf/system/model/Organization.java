package org.wyf.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("t_sys_organization")
@Data
public class Organization extends BaseEntity {

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
     * 组织等级
     */
	private Integer level;
    /**
     * PID
     */
	@TableField("parent_id")
	private Integer parentId;
    /**
     * 所属集团
     */
	@TableField("group_id")
	private Integer groupId;
    /**
     * 企业描述
     */
	private String introduction;
    /**
     * 来源标记
     */
	private Integer flag;
    /**
     * 关联组织id
     */
	@TableField("relation_org_id")
	private Integer relationOrgId;
    /**
     * 状态
     */
	private Integer status;
    /**
     * 备注
     */
	private String comments;




}
