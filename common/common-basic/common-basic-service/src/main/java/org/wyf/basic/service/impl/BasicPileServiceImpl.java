package org.wyf.basic.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.wyf.basic.entity.BasicPile;
import org.wyf.basic.entity.BasicStation;
import org.wyf.basic.mapper.BasicPileMapper;
import org.wyf.basic.service.IBasicPileService;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.springframework.stereotype.Service;
import org.wyf.basic.service.IBasicPileService;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;

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
public class BasicPileServiceImpl extends ServiceImpl<BasicPileMapper, BasicPile> implements IBasicPileService {

    @Override
    public List<BasicPile> selectPileInfo(Page<BasicPile> page, RequestDTO<BasicPile> requestDTO) throws Exception {
        Map<String,Object> map = new HashMap<>(10);
        map.put("orgIds",requestDTO.getOrgIds());
        map.put("key",requestDTO.getKey());
        return baseMapper.selectPileInfo(page,map);
    }

    @Override
    public  List<BasicStation> getStationInfo() {
        return baseMapper.getStationInfo();
    }

    @Override
    public ResultDTO insertBasicPile(BasicPile basicPile) {
        if(StrUtil.isBlank(basicPile.getName())){
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        QueryWrapper<BasicPile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("station_id",basicPile.getStationId());
        queryWrapper.eq("name",basicPile.getName());
        int countPile = this.baseMapper.selectCount(queryWrapper);
        if(countPile>0){
            return ResultDTO.factory(ResultCodeEnum.PILE_EXIST);
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_no",basicPile.getDeviceNo());
        int countDevice = this.baseMapper.selectCount(queryWrapper);
        if(countDevice>0){
            return ResultDTO.factory(ResultCodeEnum.DEVICE_BINDED_PILE);
        }
        if(this.save(basicPile)){
            return ResultDTO.resultFactory(OperationEnum.INSERT_SUCCESS);
        }else{
            return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
        }
    }

    @Override
    public ResultDTO updateBasicPile(BasicPile basicPile) {
        QueryWrapper<BasicPile> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("id",basicPile.getId());
        queryWrapper.eq("station_id",basicPile.getStationId());
        queryWrapper.eq("name",basicPile.getName());
        int count  = this.baseMapper.selectCount(queryWrapper);
        if(count>0){
            return ResultDTO.factory(ResultCodeEnum.PILE_EXIST);
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("id",basicPile.getId());
        queryWrapper.eq("device_no",basicPile.getDeviceNo());
        int countDevice = this.baseMapper.selectCount(queryWrapper);
        if(countDevice>0) {
            return ResultDTO.factory(ResultCodeEnum.DEVICE_BINDED_PILE);
        }
        if(this.updateById(basicPile)){
            return ResultDTO.resultFactory(OperationEnum.UPDATE_SUCCESS);
        }else{
            return ResultDTO.resultFactory(OperationEnum.UPDATE_ERROR);
        }

    }

}
