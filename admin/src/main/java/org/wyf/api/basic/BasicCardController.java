package org.wyf.api.basic;


import org.wyf.basic.entity.BasicQrcode;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wyf.common.controller.BaseController;
import org.wyf.basic.entity.BasicCard;
import org.wyf.basic.service.IBasicCardService;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wyf
 * @since 2019-11-26
 */
@RestController
@RequestMapping("/basic/basic-card")
public class BasicCardController extends BaseController<BasicCard,IBasicCardService> {


@Override
public String insertRole() {
        return "basic:basiccard:insert";
        }

@Override
public String updateRole() {
        return "basic:basiccard:update";
        }

@Override
public String deleteRole() {
        return "basic:basiccard:delete";
        }

@Override
public String viewRole() {
        return "basic:basiccard:view";
        }


        @PostMapping("insertBasicCard")
        public ResultDTO insertBasicCard(@RequestBody RequestDTO<BasicCard> requestDTO){
                hasPermission(this.insertRole());
                try {
                        return  service.insertBasicCard(requestDTO.getBody());
                }catch (Exception ex){
                        ex.printStackTrace();
                }
                return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
        }

        @PostMapping("batchInsertBasicCard")
        public ResultDTO batchInsertBasicCard(@RequestBody RequestDTO<List<BasicCard>> requestDTO){
                hasPermission(this.insertRole());
                try {
                        return  service.batchInsertBasicCard(requestDTO.getBody());
                }catch (Exception ex){
                        ex.printStackTrace();
                }
                return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
        }





}

