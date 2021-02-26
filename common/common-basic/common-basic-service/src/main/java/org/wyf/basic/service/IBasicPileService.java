package org.wyf.basic.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wyf.basic.entity.BasicPile;
import org.wyf.basic.entity.BasicStation;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf
 * @since 2019-11-14
 */
public interface IBasicPileService extends IService<BasicPile> {

    /**
     * 获取桩列表
     * @param page
     * @param requestDTO
     * @return
     * @throws Exception
     */
    List<BasicPile> selectPileInfo(Page<BasicPile> page, RequestDTO<BasicPile> requestDTO) throws Exception;

    /**
     * 获取站名
     * @return
     */
    List<BasicStation> getStationInfo();


    /**
     *  新增桩
     *  1.站名判重
     *  2.设备号判重
     * @param basicPile
     * @return
     */
    ResultDTO insertBasicPile(BasicPile basicPile);


    /**
     *  修改桩
     *  1.站名判重
     *  2.设备号判重
     * @param basicPile
     * @return
     */
    ResultDTO updateBasicPile( BasicPile basicPile);

}
