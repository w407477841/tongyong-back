package org.wyf.api.basic;


import org.wyf.common.constant.OperationEnum;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wyf.common.controller.BaseController;
import org.wyf.basic.entity.BasicQrcode;
import org.wyf.basic.service.IBasicQrcodeService;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wangyifei
 * @since 2019-12-13
 */
@RestController
@RequestMapping("/basic/basic-qrcode")
public class BasicQrcodeController extends BaseController<BasicQrcode,IBasicQrcodeService> {


@Override
public String insertRole() {
        return "basic:basicqrcode:insert";
        }

@Override
public String updateRole() {
        return "basic:basicqrcode:update";
        }

@Override
public String deleteRole() {
        return "basic:basicqrcode:delete";
        }

@Override
public String viewRole() {
        return "basic:basicqrcode:view";
        }



        @PostMapping("insertBasicQrcode")
        public  ResultDTO insertBasicQrcode(@RequestBody RequestDTO<BasicQrcode> requestDTO){
                hasPermission(this.insertRole());
                try {
                       return  service.insertBasicQrcode(requestDTO.getBody());
                }catch (Exception ex){
                        ex.printStackTrace();
                }
                return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
        }

        @PostMapping("updateBasicQrcode")
        public  ResultDTO updateBasicQrcode(@RequestBody RequestDTO<BasicQrcode> requestDTO){
                hasPermission(this.updateRole());
                try {
                        return  service.updateBasicQrcode(requestDTO.getBody());
                }catch (Exception ex){
                        ex.printStackTrace();
                }
                return ResultDTO.resultFactory(OperationEnum.UPDATE_ERROR);
        }
        @PostMapping("getPiles")
        public ResultDTO getPiles(){
                hasPermission(this.viewRole());
                try {
                        return  service.getPiles();
                }catch (Exception ex){
                        ex.printStackTrace();
                }
                return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);

        }



}

