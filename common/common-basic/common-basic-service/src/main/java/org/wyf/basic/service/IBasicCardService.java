package org.wyf.basic.service;

import org.wyf.basic.entity.BasicCard;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wyf.basic.entity.BasicQrcode;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.common.dto.ResultDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf
 * @since 2019-11-26
 */
public interface IBasicCardService extends IService<BasicCard> {

    /**
     * 用户解绑虚拟卡
     * @param cardno
     */
    void updateUserid(String cardno);

    /**
     *  新增
     *  卡号判重
     * @param body
     * @return
     */
    ResultDTO insertBasicCard(BasicCard body);


    ResultDTO batchInsertBasicCard(List<BasicCard> body);
}
