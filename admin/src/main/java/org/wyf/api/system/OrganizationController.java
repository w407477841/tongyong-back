package org.wyf.api.system;
import	java.rmi.server.Operation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.wyf.common.constant.Const;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.controller.BaseController;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.security.service.SecurityService;
import org.wyf.system.ConstSystem;
import org.wyf.system.model.Organization;
import org.wyf.system.model.OrganizationUser;
import org.wyf.system.service.service.IOrganizationService;
import org.wyf.system.service.service.IOrganizationUserService;
import org.wyf.system.vo.OrganizationUserVo;
import org.wyf.system.vo.OrganizationVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wyf.security.service.SecurityService;
import org.wyf.system.ConstSystem;
import org.wyf.system.model.Organization;
import org.wyf.system.model.OrganizationUser;
import org.wyf.system.vo.OrganizationUserVo;
import org.wyf.system.vo.OrganizationVo;

import java.util.List;

@RestController
@RequestMapping("/system/organization")
public class OrganizationController extends BaseController<Organization, IOrganizationService> {
	@Autowired
    SecurityService securityService;

	@Autowired
	IOrganizationUserService iOrganizationUserService;

	@Override
	public String insertRole() {
		return "system:org:insert";
	}

	@Override
	public String updateRole() {
		return "system:org:update";
	}

	@Override
	public String deleteRole() {
		return "system:org:delete";
	}

	@Override
	public String viewRole() {
		return "system:org:view";
	}

	@ApiOperation("获取部门信息")
	@PostMapping(value = "/selectOrganizationInfo")
	public ResultDTO<List<OrganizationVo>> selectOrganizationInfo(@RequestBody RequestDTO<Organization> t) {
		try {
			return new ResultDTO<>(true,service.selectOrganizationInfo(t));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResultDTO<>(false);
	}
	@ApiOperation("选择集团")
	@PostMapping(value =  "chooseOrg")
	public ResultDTO<Integer> chooseOrg(@RequestBody RequestDTO<Integer> t){
		try {
			//删除当前用户之前选择的集团
			securityService.removeOrgids(ConstSystem.currUser.get().getId());

			securityService.updateOrgId(
					Const.token.get(),
					t.getBody()
						);
			securityService.updateOrgids(ConstSystem.currUser.get().getId());
			return new ResultDTO<>(true);
		}catch (Exception e){
			e.printStackTrace();
		}
		return  new ResultDTO<>(false);
	}

	@ApiOperation("删除部门用户")
	@PostMapping(value = "/deleteOrgUser")
	public ResultDTO<OrganizationUser> deleteOrgUser(@RequestBody RequestDTO<OrganizationUser> requestDTO) {
		try {
			List<Integer> ids = requestDTO.getIds();
			if(iOrganizationUserService.removeByIds(ids)) {
				return ResultDTO.resultFactory(OperationEnum.DELETE_SUCCESS);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ResultDTO.resultFactory(OperationEnum.DELETE_ERROR);
	}

	@ApiOperation("新增部门用户")
	@PostMapping(value = "/insertOrgUser")
	public ResultDTO<OrganizationUserVo> insertOrgUser(@RequestBody RequestDTO<OrganizationUserVo> requestDTO) {
		try {
			List<OrganizationUser> list = requestDTO.getBody().getOrgUsers();
			List<OrganizationUser> organizationUsers = requestDTO.getBody().getOrgUsers();
			for(int i = 0;i < list.size();i++) {
				QueryWrapper<OrganizationUser> wrapper = new QueryWrapper<>();
				wrapper.eq("org_id",list.get(i).getOrgId());
				wrapper.eq("user_id",list.get(i).getUserId());
				List<OrganizationUser> organizationUserList = iOrganizationUserService.list(wrapper);
				if(organizationUserList.size() > 0) {
					organizationUsers.remove(i);
				}
			}
			if(organizationUsers.size() > 0) {
				if(iOrganizationUserService.saveBatch(organizationUsers)) {
					return ResultDTO.resultFactory(OperationEnum.INSERT_SUCCESS);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
	}

	@PostMapping("/selectOrganization")
	@ApiOperation("获取当前用户下项目部")
	public ResultDTO<List<Organization>> selectOrganization(@RequestBody RequestDTO<Organization> requestDTO) {
		try {
			return new ResultDTO<>(true,service.selectOrganization(Const.orgIds.get()));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResultDTO<>(false);
	}

	@PostMapping("/selectUnderOrganization")
	@ApiOperation("获取当前用户下项目部")
	public ResultDTO<List<Organization>> selectUnderOrganization(@RequestBody RequestDTO<Organization> requestDTO) {
		try {
			return new ResultDTO<>(true,service.selectUnderOrganization());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResultDTO<>(false);
	}

	@Override
	@PostMapping("insert")
	public ResultDTO<Organization> insert(@RequestBody RequestDTO<Organization> t) {

		hasPermission(insertRole());


		try {
			if (service.save(t.getBody())) {


				securityService.updateOrgids(ConstSystem.currUser.get().getId());

				return ResultDTO.resultFactory(OperationEnum.INSERT_SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
	}


}
