package org.wyf.aftersale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @author yaoy
 * @since 2019-12-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_as_idea")
public class AsIdea extends BaseEntity {


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 意见内容
     */
    private String ideaContent;

    /**
     * 处理内容
     */
    private String feedbackContent;

    /**
     * 处理状态（0已上报，1处理中，已处理）
     */
    private Integer status;

    /**
     * 反馈状态（0未解决，1已解决）
     */
    private Integer feedbackStatus;

    /**
     * 备注
     */
    private String comments;


}
