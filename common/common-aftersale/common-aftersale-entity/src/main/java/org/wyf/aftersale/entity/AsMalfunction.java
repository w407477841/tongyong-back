package org.wyf.aftersale.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.wyf.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import org.wyf.common.entity.BaseEntity;

/**
 * <p>
 * 
 * </p>
 *
 * @author chenlin
 * @since 2019-12-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_as_malfunction")
public class AsMalfunction extends BaseEntity {
    /**
     * id主键
     * */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * deviceNo 设备号
     */
    private String deviceNo;
    /**
     * plugNo 插头号
     */
    private String plugNo;
    /**
     * reportTime 上报时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date reportTime;
    /**
     * repairTime 维修时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date repairTime;
    /**
     * malfunctionContent 故障内容
     */
    private String malfunctionContent;
    /**
     * feedbackContent 处理内容
     */
    private String feedbackContent;
    /**
     * status 记录状态（0已上报，1处理中，已处理）
     */
    private Integer status;
    /**
     * feedbackStatus 反馈状态（0未解决，1已解决）
     */
    private Integer feedbackStatus;
    /**
     * comments 备注
     */
    private String comments;

    /**
     * phone 手机号
     */
    private String phone;
}
