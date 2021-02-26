package org.wyf.order.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.wyf.common.dto.RequestDTO;
import org.wyf.order.entity.OrderRecord;
import org.wyf.order.mapper.OrderRecordMapper;
import org.wyf.order.service.IOrderRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wyf.common.dto.RequestDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wyf
 * @since 2019-11-21
 */
@Service
public class OrderRecordServiceImpl extends ServiceImpl<OrderRecordMapper, OrderRecord> implements IOrderRecordService {
    @Override
    public List<OrderRecord> selectOrder(Page<OrderRecord> page, RequestDTO<OrderRecord> requestDTO) throws Exception {
        Map<String,Object> map = new HashMap<>(10);
        map.put("orgIds",requestDTO.getOrgIds());
        map.put("key",requestDTO.getKey());
        return baseMapper.selectOrder(page,map);
    }
}
