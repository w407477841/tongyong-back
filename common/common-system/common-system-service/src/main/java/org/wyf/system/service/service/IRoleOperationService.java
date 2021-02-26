package org.wyf.system.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.vo.RoleOperationVo;
import org.wyf.system.model.RoleOperation;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.model.RoleOperation;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf123
 * @since 2018-08-13
 */
public interface IRoleOperationService extends IService<RoleOperation> {
    /**
     * 获取角色拥有的权限
     * @param requestDTO
     * @return
     * @throws Exception
     */
    List<RoleOperation> selectRoleOwnedOperation(RequestDTO<RoleOperationVo> requestDTO) throws Exception;
}
