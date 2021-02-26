package org.wyf.basic.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.wyf.basic.entity.BasicCard;
import org.wyf.basic.entity.BasicQrcode;
import org.wyf.basic.mapper.BasicCardMapper;
import org.wyf.basic.service.IBasicCardService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.ResultDTO;
import org.springframework.stereotype.Service;
import org.wyf.basic.service.IBasicCardService;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.ResultDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wyf
 * @since 2019-11-26
 */
@Service
public class BasicCardServiceImpl extends ServiceImpl<BasicCardMapper, BasicCard> implements IBasicCardService {

    @Override
    public void updateUserid(String cardno) {
        Map<String, Object> map = new HashMap<>();
        map.put("card_no", cardno);
        baseMapper.updateUserid(map);
    }

    @Override
    public ResultDTO insertBasicCard(BasicCard body) {

        if(StrUtil.isBlank(body.getCardNo())){
            return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
        }

        QueryWrapper<BasicCard> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("card_no",body.getCardNo());
        int count  = this.count(queryWrapper);
        if(count > 0){
            return ResultDTO.factory(ResultCodeEnum.CARDNO_EXIST);
        }


        if(this.save(body)){
            return ResultDTO.resultFactory(OperationEnum.INSERT_SUCCESS);
        }else{
            return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
        }
    }

    @Override
    public ResultDTO batchInsertBasicCard(List<BasicCard> body) {
        if(CollectionUtil.isEmpty(body)){
            return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
        }
        List<BasicCard> insertList = new ArrayList<>(body.size()*2);
        QueryWrapper<BasicCard> queryWrapper ;
        for (BasicCard basicCard : body) {
            queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("card_no",basicCard.getCardNo());
            int count  = this.count(queryWrapper);
            if(count == 0){
                insertList.add(basicCard);
            }
        }

        if(this.saveBatch(insertList)){
            return ResultDTO.resultFactory(OperationEnum.INSERT_SUCCESS);
        }else{
            return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
        }


    }


}
