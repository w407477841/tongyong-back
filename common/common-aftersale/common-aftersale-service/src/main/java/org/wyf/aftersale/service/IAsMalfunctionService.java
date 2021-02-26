package org.wyf.aftersale.service;

import org.wyf.aftersale.entity.AsMalfunction;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wyf.common.dto.ResultDTO;
import org.wyf.common.dto.ResultDTO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chenlin
 * @since 2019-12-13
 */
public interface IAsMalfunctionService extends IService<AsMalfunction> {
    /**
     * 修改故障报修
     * 处理报修时间
     * @param asMalfunction
     * @return
     * */
    ResultDTO updateMal(AsMalfunction asMalfunction);
}
