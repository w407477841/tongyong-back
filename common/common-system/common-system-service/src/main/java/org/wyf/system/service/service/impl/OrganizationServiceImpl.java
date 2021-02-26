package org.wyf.system.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.wyf.common.constant.Const;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.utils.TreeUtils;
import org.wyf.system.ConstSystem;
import org.wyf.system.mapper.OrganizationMapper;
import org.wyf.system.model.Organization;
import org.wyf.system.service.service.IOrganizationService;
import org.wyf.system.vo.OrgVO;
import org.wyf.system.vo.OrganizationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.wyf.common.constant.Const;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.utils.TreeUtils;
import org.wyf.system.mapper.OrganizationMapper;
import org.wyf.system.model.Organization;
import org.wyf.system.vo.OrgVO;
import org.wyf.system.vo.OrganizationVo;

import java.io.Serializable;
import java.util.ArrayList;
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
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization> implements IOrganizationService {

    @Override
    public List<OrganizationVo> selectOrganizationInfo(RequestDTO<Organization> requestDTO) {
        Map<String,Object> map = new HashMap<>(10);
        map.put("orgIds",requestDTO.getOrgIds());
        List<OrganizationVo> list = baseMapper.selectOrganizationInfo(map);
        TreeUtils<OrganizationVo> treeUtils = new TreeUtils<>();
        treeUtils.rootDir(list);
        return treeUtils.getTreeList();
    }

    @Override
    public boolean save(Organization entity) {
        boolean isSuccess = super.save(entity);
        return isSuccess;
    }

    @Override
    public List<Organization> getByUserId(Integer id) {
        List<Organization> orgs =   baseMapper.getByUserId(id);
        if(CollectionUtils.isEmpty(orgs)){
            orgs = new ArrayList<>();
        }
        return orgs;
    }

    @Override
    public List<Integer> getOrgsByParent(Integer parentId) {

        List<OrgVO>  orgs =   baseMapper.selectALlOrg();
        TreeUtils<OrgVO> utils= new TreeUtils<OrgVO>();
           utils.rootDirWithIds(orgs,parentId);
          orgs  =    utils.getList(true);
         List<Integer>  orgids =  new ArrayList<>();
         for(OrgVO org:orgs){
             orgids.add(org.getId());
         }
        return orgids;
    }

    @Override
    public List<Organization> selectOrganization(List<Integer> ids) throws Exception {
        Map<String,Object> map = new HashMap<>(10);
        map.put("list",ids);
        return baseMapper.selectOrganization(map);
    }

    @Override
    public List<Organization> selectUnderOrganization() throws Exception {
        List<Integer> ids = Const.orgIds.get();
        QueryWrapper<Organization> wrapper = new QueryWrapper<>();
        wrapper.in("id",ids);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean removeById(Serializable id) {
        Map<String,Object> map = new HashMap<>(10);
        map.put("orgIds", Const.orgIds.get());
        List<OrganizationVo> list = baseMapper.selectOrganizationInfo(map);
        deleteChildren(id,list);
        return super.removeById(id);
    }

    public void deleteChildren(Serializable id,List<OrganizationVo> organizationVos) {
        for(int i = 0;i < organizationVos.size();i++) {
            if(id.equals(organizationVos.get(i).getPid())) {
                deleteChildren(organizationVos.get(i).getId(),organizationVos);
                baseMapper.deleteById(organizationVos.get(i).getId());
            }
        }
    }
}
