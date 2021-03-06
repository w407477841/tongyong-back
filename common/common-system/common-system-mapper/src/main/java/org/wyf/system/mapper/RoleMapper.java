package org.wyf.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.wyf.system.model.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.wyf.system.model.Role;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wyf
 * @since 2018-08-16
 */
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 获取角色信息
     * @param page
     * @param map
     * @return
     * @throws Exception
     */
    List<Role> selectRoleInfo(Page<Role> page,@Param("map") Map<String, Object> map) throws Exception;

    /**
     * 根据用户id获取角色名称
     * @param map
     * @return
     * @throws Exception
     */
    List<String> selectRoleNameByUserId(Map<String,Object> map) throws Exception;
}
