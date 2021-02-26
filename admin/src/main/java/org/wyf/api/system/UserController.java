package org.wyf.api.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.wyf.common.constant.Const;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.controller.BaseController;
import org.wyf.common.dto.DataDTO;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.system.model.User;
import org.wyf.system.service.service.IUserRoleService;
import org.wyf.system.service.service.IUserService;
import org.wyf.system.vo.UserVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wyf.system.model.User;
import org.wyf.system.vo.UserVo;

import java.util.List;

@RestController
@RequestMapping("/system/user")
public class UserController extends BaseController<User, IUserService> {
	@Autowired
	IUserRoleService iUserRoleService;

	@Override
	public String insertRole() {
		return "system:user:insert";
	}
	@Override
	public String updateRole() {
		return "system:user:update";
	}
	@Override
	public String deleteRole() {
		return "system:user:delete";
	}
	@Override
	public String viewRole() {
		return "system:user:view";
	}

	@ApiOperation("获取用户")
	@PostMapping("selectList2")
	public ResultDTO<DataDTO<List<User>>> selectList2(@RequestBody RequestDTO<User> t){
		Page<User> page = new Page<>(t.getPageNum(), t.getPageSize());
		List<User>   ts=   service.page(page).getRecords();
		return new ResultDTO<DataDTO<List<User>>>(true,DataDTO.factory(ts,page.getTotal()));
	}

	@ApiOperation("新增用户")
	@PostMapping("/insertUser")
	public ResultDTO<UserVo> insertUser(@RequestBody RequestDTO<UserVo> requestDTO) {
	 	try {
			if(service.insertUser(requestDTO.getBody())) {
				return ResultDTO.resultFactory(OperationEnum.INSERT_SUCCESS);
			}
		}catch (Exception e) {
	 		e.printStackTrace();
	 		String message = e.getMessage();
	 		if("登录名重复".equals(message)) {
				return ResultDTO.factory(ResultCodeEnum.LOGIN_NAME_REPEAT);
			}else if("手机号重复".equals(message)) {
	 			return ResultDTO.factory(ResultCodeEnum.PHONE_REPEAT);
			}
		}
		return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
	}

	@ApiOperation("修改用户")
	@PostMapping("/updateUser")
	public ResultDTO<UserVo> updateUser(@RequestBody RequestDTO<UserVo> requestDTO) {
		try {
			if(service.updateUser(requestDTO.getBody())) {
				return ResultDTO.resultFactory(OperationEnum.UPDATE_SUCCESS);
			}
		}catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			if("登录名重复".equals(message)) {
				return ResultDTO.factory(ResultCodeEnum.LOGIN_NAME_REPEAT);
			}else if("手机号重复".equals(message)) {
				return ResultDTO.factory(ResultCodeEnum.PHONE_REPEAT);
			}
		}
		return ResultDTO.resultFactory(OperationEnum.UPDATE_ERROR);
	}

	@ApiOperation("获取用户")
	@PostMapping("/selectUserInfo")
	public ResultDTO<DataDTO<List<UserVo>>> selectUserInfo(@RequestBody RequestDTO<User> requestDTO) {
		try {
			Page<User> page = new Page<>(requestDTO.getPageNum(),requestDTO.getPageSize());
			List<UserVo> list = service.selectUserInfo(page,requestDTO);
			return new ResultDTO<DataDTO<List<UserVo>>>(true,DataDTO.factory(list,page.getTotal()));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResultDTO<>(false);
	}

	@ApiOperation("根据部门id获取用户")
	@PostMapping(value = "/selectUserByOrgId")
	public ResultDTO<DataDTO<List<User>>> selectUserByOrgId(@RequestBody RequestDTO<User> requestDTO) {
		try {
			Page<User> page = new Page<>(requestDTO.getPageNum(),requestDTO.getPageSize());
			List<User> list = service.selectUserByOrgId(page,requestDTO);
			return new ResultDTO<>(true,DataDTO.factory(list,page.getTotal()));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResultDTO<>(false);
	}

	@ApiOperation("获取不在部门中的用户")
	@PostMapping(value = "/selectUserNotInOrg")
	public ResultDTO<DataDTO<List<User>>> selectUserNotInOrg(@RequestBody RequestDTO<User> requestDTO) {
		try {
			Page<User> page = new Page<>(requestDTO.getPageNum(),requestDTO.getPageSize());
			List<User> list = service.selectUserNotInOrg(page,requestDTO);
			return new ResultDTO<>(true,DataDTO.factory(list,page.getTotal()));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResultDTO<>(false);
	}

	@ApiOperation("根据id获取用户")
	@PostMapping(value = "/selectUserById")
	public ResultDTO<UserVo> selectUserById(@RequestBody RequestDTO<User> requestDTO) {
		try {
			return new ResultDTO<>(true,service.selectUserById(requestDTO));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResultDTO<>(false);
	}

	@ApiOperation("修改密码")
	@PostMapping(value = "/changePWD")
	public ResultDTO<User> changePWD(@RequestBody RequestDTO<UserVo> requestDTO) {
		try {
			if(service.changePWD(requestDTO)) {
				return ResultDTO.resultFactory(OperationEnum.UPDATE_SUCCESS);
			}
		}catch (Exception e) {
			e.printStackTrace();
			if("原密码错误".equals(e.getMessage())) {
				return ResultDTO.factory(ResultCodeEnum.PASSWORD_WRONG);
			}
		}
		return ResultDTO.resultFactory(OperationEnum.UPDATE_ERROR);
	}

	@ApiOperation("获取人员不分页")
	@PostMapping("/selectUser")
	public ResultDTO<List<User>> selectUser(@RequestBody RequestDTO<User> requestDTO) {
		try {
			QueryWrapper<User> wrapper = new QueryWrapper<>();
			wrapper.in("org_id",requestDTO.getOrgIds());
			List<User> users = service.list(wrapper);
			return new ResultDTO<>(true,users);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResultDTO<>(false);
	}

	@ApiOperation("获取人员不分页")
	@PostMapping("/selectUserByLogin")
	public ResultDTO<List<User>> getUserByLogin() {
		try {
			QueryWrapper<User> wrapper = new QueryWrapper<>();
			wrapper.in("org_id",Const.orgIds.get());
			List<User> users = service.list(wrapper);
			return new ResultDTO<>(true,users);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResultDTO<>(false);
	}
}
