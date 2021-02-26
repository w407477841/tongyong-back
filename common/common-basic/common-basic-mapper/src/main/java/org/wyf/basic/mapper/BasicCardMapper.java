package org.wyf.basic.mapper;

import org.wyf.basic.entity.BasicCard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wyf
 * @since 2019-11-26
 */
public interface BasicCardMapper extends BaseMapper<BasicCard> {

    /**
     * card解绑
     * @param map
     * @return
     * @throws Exception
     */
    Integer updateUserid(Map<String,Object> map);
}
