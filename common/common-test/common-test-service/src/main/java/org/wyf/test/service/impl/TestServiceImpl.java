package org.wyf.test.service.impl;

import org.wyf.test.entity.Test;
import org.wyf.test.mapper.TestMapper;
import org.wyf.test.service.ITestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.wyf.test.service.ITestService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wyf
 * @since 2019-11-12
 */
@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements ITestService {

}
