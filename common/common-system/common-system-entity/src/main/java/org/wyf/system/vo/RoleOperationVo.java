package org.wyf.system.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import org.wyf.system.model.RoleOperation;
import lombok.Data;
import org.wyf.system.model.RoleOperation;

import java.util.List;

/***
 *@author:jixiaojun
 *DATE:2018/9/7
 *TIME:10:47
 */
@Data
public class RoleOperationVo extends RoleOperation {
    /**
     * 角色权限集合
     */
    @TableField(exist = false)
    private List<RoleOperation> roleOperations;

    /**
     * 权限id
     */
    @TableField(exist = false)
    private List<Integer> operIds;
}
