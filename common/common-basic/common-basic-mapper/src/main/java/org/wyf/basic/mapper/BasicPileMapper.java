package org.wyf.basic.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.wyf.basic.entity.BasicPile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wyf.basic.entity.BasicStation;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wyf
 * @since 2019-11-14
 */
public interface BasicPileMapper extends BaseMapper<BasicPile> {

    /**
     * 获取桩列表
     * @param page
     * @param map
     * @return
     * @throws Exception
     */
    List<BasicPile> selectPileInfo(Page<BasicPile> page, @Param("map") Map<String, Object> map) throws Exception;

    /**
     * 获取站名
     * @return
     */
    List<BasicStation> getStationInfo();
}
