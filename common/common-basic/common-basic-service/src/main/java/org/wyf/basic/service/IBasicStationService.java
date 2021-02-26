package org.wyf.basic.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wyf.basic.entity.BasicStation;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.model.Organization;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.model.Organization;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf
 * @since 2019-11-14
 */
public interface IBasicStationService extends IService<BasicStation> {
    /**
     * 获取物业信息
     * @return
     */
    List<Organization> getOrgInfo();

    /**
     * 获取站点信息列表
     * @return
     */
    List<BasicStation> selectStationList(Page<BasicStation> page, RequestDTO<BasicStation> requestDTO);
}
