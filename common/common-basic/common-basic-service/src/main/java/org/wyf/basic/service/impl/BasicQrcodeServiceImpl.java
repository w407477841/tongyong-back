package org.wyf.basic.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.wyf.basic.entity.BasicPile;
import org.wyf.basic.entity.BasicQrcode;
import org.wyf.basic.mapper.BasicQrcodeMapper;
import org.wyf.basic.service.IBasicPileService;
import org.wyf.basic.service.IBasicQrcodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wyf.basic.service.IBasicPileService;
import org.wyf.basic.service.IBasicQrcodeService;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.ResultDTO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangyifei
 * @since 2019-12-13
 */
@Service
public class BasicQrcodeServiceImpl extends ServiceImpl<BasicQrcodeMapper, BasicQrcode> implements IBasicQrcodeService {

    @Autowired
    private IBasicPileService pileService;

    @Override
    public ResultDTO insertBasicQrcode(BasicQrcode basicQrcode) {

        if(StrUtil.isBlank(basicQrcode.getQrcode())){
            return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
        }
        QueryWrapper<BasicQrcode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("qrcode",basicQrcode.getQrcode());
        int countQrcode = this.baseMapper.selectCount(queryWrapper);
        if(countQrcode>0){
            return ResultDTO.factory(ResultCodeEnum.QRCODE_EXIST);
        }
        if(StrUtil.isNotBlank(basicQrcode.getDeviceNo())){
             queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("device_no",basicQrcode.getDeviceNo());
            int countDevice = this.baseMapper.selectCount(queryWrapper);
            if(countDevice>0){
                return ResultDTO.factory(ResultCodeEnum.DEVICE_BINDED_QRCODE);
            }
        }
        if(this.save(basicQrcode)){
            return ResultDTO.resultFactory(OperationEnum.INSERT_SUCCESS);
        }else{
            return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
        }

    }

    @Override
    public ResultDTO updateBasicQrcode(BasicQrcode basicQrcode) {

            if(StrUtil.isNotBlank(basicQrcode.getDeviceNo())){
                QueryWrapper<BasicQrcode> queryWrapper = new QueryWrapper<>();
                queryWrapper.ne("id",basicQrcode.getId());
                queryWrapper.eq("device_no",basicQrcode.getDeviceNo());
                int count  = this.baseMapper.selectCount(queryWrapper);
                if(count>0){
                    return ResultDTO.factory(ResultCodeEnum.DEVICE_BINDED_QRCODE);
                }
            }

            UpdateWrapper<BasicQrcode> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("device_no",basicQrcode.getDeviceNo());
            updateWrapper.eq("id",basicQrcode.getId());

            if(this.update(updateWrapper)){
                return ResultDTO.resultFactory(OperationEnum.UPDATE_SUCCESS);
            }else{
                return ResultDTO.resultFactory(OperationEnum.UPDATE_ERROR);
            }




    }

    @Override
    public ResultDTO getPiles() {

        QueryWrapper<BasicPile> queryWrapper = new QueryWrapper<>();


         List<Map<String,Object>> list = pileService.listMaps(queryWrapper);

        return new ResultDTO(true,list);
    }
}
