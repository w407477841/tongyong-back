package org.wyf.system.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.vo.OrganizationVo;
import org.wyf.system.model.Organization;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.model.Organization;
import org.wyf.system.vo.OrganizationVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf
 * @since 2018-08-16
 */
public interface IOrganizationService extends IService<Organization> {
    /**
     * 获取部门信息
     * @param requestDTO
     * @return
     */
    List<OrganizationVo> selectOrganizationInfo(RequestDTO<Organization> requestDTO);

    /**
     * 根据用户ID 获取组织
     * @param id
     * @return
     */
    List<Organization> getByUserId(Integer id);


    /**
     * 获取所有部门
     */
    List<Integer>  getOrgsByParent(Integer parentId);

    /**
     * 获取当前用户下的项目部
     * @param ids
     * @return
     * @throws Exception
     */
    List<Organization> selectOrganization(List<Integer> ids) throws Exception;

    /**
     * 获取当前用户下的项目部
     * @return
     * @throws Exception
     */
    List<Organization> selectUnderOrganization() throws Exception;
}
