package org.wyf.basic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @author wangyifei
 * @since 2019-12-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_basic_qrcode")
public class BasicQrcode extends BaseEntity {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String qrcode;

    /**
     * 设备号
     */
    private String deviceNo;

    /**
     * 二维码地址
     */
    private String qrcodeUrl;

    /**
     * 备注
     */
    private String comments;


}
