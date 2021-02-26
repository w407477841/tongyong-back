package org.wyf.system.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.model.Operation;
import org.wyf.system.vo.OperationVo;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.model.Operation;
import org.wyf.system.vo.OperationVo;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf123
 * @since 2018-08-13
 */
public interface IOperationService extends IService<Operation> {


    /**
     * 查询所有权限
     * @return
     */
    public String getPermissions();

    /**
     * 查询 所有菜单
     * @return
     */
    public List<Operation> getOperations();




    /**
     * 获取权限树形数据
     * @param requestDTO
     * @return
     * @throws Exception
     */
    List<OperationVo> selectTreeOperation(RequestDTO<OperationVo> requestDTO) throws Exception;

}
