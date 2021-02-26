package org.wyf.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.order.entity.OrderRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wyf.common.dto.RequestDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf
 * @since 2019-11-21
 */
public interface IOrderRecordService extends IService<OrderRecord> {
    /**
     * 获取充电订单列表
     * @param page
     * @param requestDTO
     * @return
     * @throws Exception
     * */
    List<OrderRecord> selectOrder(Page<OrderRecord> page, RequestDTO<OrderRecord> requestDTO) throws Exception;

}
