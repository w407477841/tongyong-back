package org.wyf.system.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.mapper.RoleOperationMapper;
import org.wyf.system.service.service.IRoleOperationService;
import org.wyf.system.vo.RoleOperationVo;
import org.wyf.system.model.RoleOperation;
import org.springframework.stereotype.Service;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.mapper.RoleOperationMapper;
import org.wyf.system.model.RoleOperation;

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
public class RoleOperationServiceImpl extends ServiceImpl<RoleOperationMapper, RoleOperation> implements IRoleOperationService {
    @Override
    public List<RoleOperation> selectRoleOwnedOperation(RequestDTO<RoleOperationVo> requestDTO) throws Exception {
        Map<String,Object> map = new HashMap<>(10);
        map.put("roleId",requestDTO.getBody().getRoleId());
        return baseMapper.selectRoleOwnedOperation(map);
    }
}
