package org.wyf.order.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.wyf.order.entity.OrderRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.wyf.order.entity.OrderRecord;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 *
 * @author wyf
 * @since 2019-11-21
 */
public interface OrderRecordMapper extends BaseMapper<OrderRecord> {
    /**
     * @param page
     * @param map
     * @return
     * @throws Exception
     * */
    List<OrderRecord> selectOrder(Page<OrderRecord> page,  @Param("map") Map<String, Object> map);
}
