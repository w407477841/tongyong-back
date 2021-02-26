package org.wyf.api.aftersale;


import org.wyf.common.constant.OperationEnum;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wyf.common.controller.BaseController;
import org.wyf.aftersale.entity.AsMalfunction;
import org.wyf.aftersale.service.IAsMalfunctionService;
/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author chenlin
 * @since 2019-12-13
 */
@RestController
@RequestMapping("/aftersale/as-malfunction")
public class AsMalfunctionController extends BaseController<AsMalfunction,IAsMalfunctionService> {


@Override
public String insertRole() {
        return "aftersale:asmalfunction:insert";
        }

@Override
public String updateRole() {
        return "aftersale:asmalfunction:update";
        }

@Override
public String deleteRole() {
        return "aftersale:asmalfunction:delete";
        }

@Override
public String viewRole() {
        return "aftersale:asmalfunction:view";
        }
//        /**
//         * 修改
//         * @param requestDTO
//         * @return
//         */
//        @PostMapping("updateMal")
//        public ResultDTO updateMal(@RequestBody RequestDTO<AsMalfunction> requestDTO){
//                hasPermission(this.updateRole());
//                try {
//                        return  service.updateMal(requestDTO.getBody());
//                }catch (Exception ex){
//                        ex.printStackTrace();
//                }
//                return ResultDTO.resultFactory(OperationEnum.UPDATE_ERROR);
//        }
}

