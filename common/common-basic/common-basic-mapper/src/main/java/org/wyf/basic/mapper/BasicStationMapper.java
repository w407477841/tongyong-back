package org.wyf.basic.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.wyf.basic.entity.BasicStation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.wyf.system.model.Organization;
import org.apache.ibatis.annotations.Param;
import org.wyf.system.model.Organization;

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
public interface BasicStationMapper extends BaseMapper<BasicStation> {

    /**
     * 获取物业信息
     * @return
     */
    List<Organization> getOrgInfo();

    /**
     * 获取站点信息列表
     * @return
     */
    List<BasicStation> selectStationList(Page<BasicStation> page, @Param("map") Map<String, Object> map);

}
