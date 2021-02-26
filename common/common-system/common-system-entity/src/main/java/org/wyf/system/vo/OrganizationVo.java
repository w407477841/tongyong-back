package org.wyf.system.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import org.wyf.common.utils.TreeI;
import org.wyf.system.model.Organization;
import lombok.Data;
import org.wyf.system.model.Organization;

import java.util.List;

/***
 *@author:jixiaojun
 *DATE:2018/8/21
 *TIME:10:43
 */
@Data
public class OrganizationVo extends Organization implements TreeI {
    /**
     * 子集
     */
    @TableField(exist = false)
    private List<? extends TreeI> children;

    @Override
    public Integer getPid() {
        return this.getParentId();
    }

    @Override
    public void setChildren(List<? extends TreeI> children) {
        this.children = children;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getPcode() {
        return null;
    }
}
