package org.wyf.basic.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.wyf.basic.entity.BasicStation;
import org.wyf.basic.mapper.BasicStationMapper;
import org.wyf.basic.service.IBasicStationService;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.model.Organization;
import org.springframework.stereotype.Service;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.model.Organization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wyf
 * @since 2019-11-14
 */
@Service
public class BasicStationServiceImpl extends ServiceImpl<BasicStationMapper, BasicStation> implements IBasicStationService {
    @Override
    public List<Organization> getOrgInfo() {
        return baseMapper.getOrgInfo();
    }


    @Override
    public List<BasicStation> selectStationList(Page<BasicStation> page, RequestDTO<BasicStation> requestDTO) {
        Map<String,Object> map = new HashMap<>(10);
        map.put("orgIds",requestDTO.getOrgIds());
        map.put("key",requestDTO.getKey());
        return baseMapper.selectStationList(page, map);
    }
}
