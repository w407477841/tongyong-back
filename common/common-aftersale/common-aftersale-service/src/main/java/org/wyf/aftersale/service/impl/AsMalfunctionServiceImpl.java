package org.wyf.aftersale.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.wyf.aftersale.entity.AsMalfunction;
import org.wyf.aftersale.mapper.AsMalfunctionMapper;
import org.wyf.aftersale.service.IAsMalfunctionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.dto.ResultDTO;
import org.springframework.stereotype.Service;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.dto.ResultDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chenlin
 * @since 2019-12-13
 */
@Service
public class AsMalfunctionServiceImpl extends ServiceImpl<AsMalfunctionMapper, AsMalfunction> implements IAsMalfunctionService {
    @Override
    public ResultDTO updateMal(AsMalfunction asMalfunction){
        String repairTime = asMalfunction.getRepairTime().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            asMalfunction.setRepairTime(sdf.parse(repairTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(this.updateById(asMalfunction)){
            return ResultDTO.resultFactory(OperationEnum.UPDATE_SUCCESS);
        }else{
            return ResultDTO.resultFactory(OperationEnum.UPDATE_ERROR);
        }

    }
}
