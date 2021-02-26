package org.wyf.system.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import org.wyf.system.model.Operation;
import lombok.Data;
import org.wyf.system.model.Operation;

import java.util.List;

/***
 *@author:jixiaojun
 *DATE:2018/9/6
 *TIME:11:22
 */
@Data
public class OperationVo extends Operation {
    /**
     * 子级集合
     */
    @TableField(exist = false)
    List<OperationVo> children;
}
