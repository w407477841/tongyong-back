package org.wyf.api.basic;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.wyf.basic.entity.BasicStation;
import org.wyf.basic.service.IBasicStationService;
import org.wyf.common.controller.BaseController;
import org.wyf.common.dto.DataDTO;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.system.model.Organization;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wyf.system.model.Organization;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zyh
 * @since 2019-12-13
 */
@RestController
@RequestMapping("/basic/basic-station")
public class BasicStationController extends BaseController<BasicStation,IBasicStationService> {


@Override
public String insertRole() {
        return "basic:basicstation:insert";
        }

@Override
public String updateRole() {
        return "basic:basicstation:update";
        }

@Override
public String deleteRole() {
        return "basic:basicstation:delete";
        }

@Override
public String viewRole() {
        return "basic:basicstation:view";
        }

    /**
     * 获取物业信息
     * @return
     */
    @PostMapping("/getOrgInfo")
    public ResultDTO<List<Organization>> getOrgInfo() {
        try {
            List<Organization> orgs = service.getOrgInfo();
            return new ResultDTO<>(true,orgs);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultDTO<>(false);
    }

    /**
     * 获取站点信息列表
     * @return
     */
    @PostMapping("/selectStationList")
    public ResultDTO<DataDTO<List<BasicStation>>> selectStationList(@RequestBody RequestDTO<BasicStation> requestDTO) {
        try {
            Page<BasicStation> page = new Page<>(requestDTO.getPageNum(),requestDTO.getPageSize());
            List<BasicStation> list = service.selectStationList(page,requestDTO);
            return new ResultDTO<>(true,DataDTO.factory(list,page.getTotal()));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultDTO<>(false);
    }
}

