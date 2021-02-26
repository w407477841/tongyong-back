package org.wyf.api.system;

import org.wyf.common.controller.BaseController;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.system.model.Operation;
import org.wyf.system.service.service.IOperationService;
import org.wyf.system.vo.OperationVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.wyf.system.model.Operation;
import org.wyf.system.vo.OperationVo;

import java.util.List;

/***
 *@author:jixiaojun
 *DATE:2018/9/6
 *TIME:13:58
 */
@RestController
@RequestMapping("/system/operation")
public class OperationController extends BaseController<Operation,IOperationService> {
    @Override
    public String insertRole() {
        return "system:operation:insert";
    }

    @Override
    public String updateRole() {
        return "system:operation:update";
    }

    @Override
    public String deleteRole() {
        return "system:operation:delete";
    }

    @Override
    public String viewRole() {
        return "system:operation:view";
    }

    @ApiOperation("查询权限树")
    @PostMapping("/selectTreeOperation")
    public ResultDTO<List<OperationVo>> selectTreeOperation(@RequestBody RequestDTO<OperationVo> requestDTO) {
        try {
            return new ResultDTO<>(true,service.selectTreeOperation(requestDTO));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultDTO<>(false);
    }

    


    /**
    * @author: wangyifei
    * Description: 获取当前用户的对应的当前权限
    * Date: 10:34 2018/9/7
    */
    @ApiOperation("查询左侧菜单")
    @GetMapping("getPermissions")
    public Object getLiftMenu(){

        return new ResultDTO(true, service.getPermissions());

    }

}
