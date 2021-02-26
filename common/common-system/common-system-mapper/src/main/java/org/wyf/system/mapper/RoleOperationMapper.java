package org.wyf.system.mapper;

import org.wyf.system.model.RoleOperation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wyf.system.model.RoleOperation;

import java.util.List;
import java.util.Map;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wyf123
 * @since 2018-08-13
 */
public interface RoleOperationMapper extends BaseMapper<RoleOperation> {
    /**
     * 获取角色拥有的权限
     * @param map
     * @return
     * @throws Exception
     */
    List<RoleOperation> selectRoleOwnedOperation(Map<String,Object> map) throws Exception;
}
