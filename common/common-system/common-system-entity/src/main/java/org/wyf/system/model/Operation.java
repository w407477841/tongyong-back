package org.wyf.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.wyf.common.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wyf123
 * @since 2018-08-13
 */
@Data
@TableName("t_sys_operation")
public class Operation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 上级ID
     */
    @TableField("parent_id")
    private Integer parentId;
    /**
     * 名称
     */
    private String name;
    /**
     * 等级
     */
    private Integer level;
    /**
     * url
     */
    private String url;
    /**
     * 权限字符
     */
    private String permission;
    /**
     * 类型
     */
    private String type;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String comments;



}
