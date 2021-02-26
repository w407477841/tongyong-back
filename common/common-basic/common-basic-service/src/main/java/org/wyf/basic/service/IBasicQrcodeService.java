package org.wyf.basic.service;

import org.wyf.basic.entity.BasicQrcode;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.wyf.common.dto.ResultDTO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangyifei
 * @since 2019-12-13
 */
public interface IBasicQrcodeService extends IService<BasicQrcode> {
    /**
     *  新增二维码
     *  1.二维码判重
     *  2.设备号判重
     * @param basicQrcode
     * @return
     */
    ResultDTO insertBasicQrcode(BasicQrcode basicQrcode);


    /**
     *  修改二维码，只能修改设备号
     *  1.设备号判重
     * @param basicQrcode
     * @return
     */
    ResultDTO updateBasicQrcode( BasicQrcode basicQrcode);

    /**
     * 查询 充电桩
     * @return
     */
    ResultDTO getPiles( );


}
