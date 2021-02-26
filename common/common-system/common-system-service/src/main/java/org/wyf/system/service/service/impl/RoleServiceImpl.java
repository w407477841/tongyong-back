package org.wyf.system.service.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.mapper.RoleMapper;
import org.wyf.system.service.service.IRoleService;
import org.wyf.system.model.Role;
import org.springframework.stereotype.Service;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.mapper.RoleMapper;
import org.wyf.system.model.Role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wyf
 * @since 2018-08-16
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Override
    public List<Role> selectRoleInfo(Page<Role> page, RequestDTO<Role> requestDTO) throws Exception {
        Map<String,Object> map = new HashMap<>(10);
        map.put("orgIds",requestDTO.getOrgIds());
        map.put("key",requestDTO.getKey());
        return baseMapper.selectRoleInfo(page,map);
    }
}
