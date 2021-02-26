package org.wyf.system.service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.model.Role;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.model.Role;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf
 * @since 2018-08-16
 */
public interface IRoleService extends IService<Role> {
    /**
     * 获取角色信息
     * @param page
     * @param requestDTO
     * @return
     * @throws Exception
     */
    List<Role> selectRoleInfo(Page<Role> page, RequestDTO<Role> requestDTO) throws Exception;
}
