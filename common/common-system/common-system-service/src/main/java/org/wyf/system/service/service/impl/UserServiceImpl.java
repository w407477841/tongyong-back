package org.wyf.system.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.wyf.common.constant.Const;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.ConstSystem;
import org.wyf.system.mapper.RoleMapper;
import org.wyf.system.mapper.UserMapper;
import org.wyf.system.mapper.UserRoleMapper;
import org.wyf.system.model.User;
import org.wyf.system.model.UserRole;
import org.wyf.system.service.service.IUserRoleService;
import org.wyf.system.service.service.IUserService;
import org.wyf.system.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wyf.common.constant.Const;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.ConstSystem;
import org.wyf.system.mapper.RoleMapper;
import org.wyf.system.mapper.UserMapper;
import org.wyf.system.mapper.UserRoleMapper;
import org.wyf.system.model.User;
import org.wyf.system.model.UserRole;
import org.wyf.system.vo.UserVo;

import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wyf
 * @since 2018-08-16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    UserRoleMapper userRoleMapper;
    @Autowired
    IUserRoleService iUserRoleService;
    @Autowired
    RoleMapper roleMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertUser(UserVo userVo) throws Exception {
        boolean isSuccess;
        Map<String,Object> map = new HashMap<>(10);
        map.put("code",userVo.getCode());
        map.put("phone",userVo.getPhone());
        User user = baseMapper.checkUser(map);
        if(user != null) {
            throw new RuntimeException("登录名重复");
        }
        User user1 = baseMapper.checkPhone(map);
        if(user1 != null) {
            throw new RuntimeException("手机号重复");
        }
        //加密密码
        String password = passwordEncoder.encode("123456");
        userVo.setPassword(password);
        userVo.setOrgId(Const.orgId.get());
        userVo.setIsDel(0);
        userVo.setCreateUser(ConstSystem.currUser.get().getId());
        userVo.setCreateTime(new Date());
        userVo.setStatus(1);
        isSuccess = this.save(userVo);
        QueryWrapper<UserRole> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("user_id",userVo.getId());
        userRoleMapper.delete(wrapper1);
        List<UserRole> userRoles = userVo.getUserRoleList();
        for(int i = 0;i < userRoles.size();i++) {
            userRoles.get(i).setUserId(userVo.getId());
        }
        if(userRoles.size() > 0) {
            iUserRoleService.saveBatch(userRoles);
        }
        return isSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateUser(UserVo userVo) throws Exception {
        userVo.setPassword(null);
        Map<String,Object> map = new HashMap<>(10);
        map.put("code",userVo.getCode());
        map.put("id",userVo.getId());
        map.put("phone",userVo.getPhone());
        User user = baseMapper.checkUser(map);
        if(user != null) {
            throw new RuntimeException("登录名重复");
        }
        User user1 = baseMapper.checkPhone(map);
        if(user1 != null) {
            throw new RuntimeException("手机号重复");
        }
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userVo.getId());
        userRoleMapper.delete(wrapper);
        List<UserRole> userRoles = userVo.getUserRoleList();
        for(int i = 0;i < userRoles.size();i++) {
            userRoles.get(i).setUserId((userVo.getId()));
        }
        if(userRoles.size() > 0) {
            iUserRoleService.saveBatch(userRoles);
        }
        return retBool(this.baseMapper.updateById(userVo));
    }

    @Override
    public List<UserVo> selectUserInfo(Page<User> page, RequestDTO<User> requestDTO) throws Exception {
        Map<String,Object> map = new HashMap<>(10);
        map.put("orgIds",requestDTO.getOrgIds());
        map.put("key",requestDTO.getKey());
        map.put("flag",1);
        return baseMapper.selectUserInfo(page,map);
    }

    @Override
    public List<User> selectUserByOrgId(Page<User> page,RequestDTO<User> requestDTO) throws Exception {
        Map<String,Object> map = new HashMap<>(10);
        map.put("id",requestDTO.getId());
        map.put("key",requestDTO.getKey());
        return baseMapper.selectUserByOrgId(page,map);
    }

    @Override
    public List<User> selectUserNotInOrg(Page<User> page, RequestDTO<User> requestDTO) throws Exception {
        Map<String,Object> map = new HashMap<>(10);
        map.put("orgIds",requestDTO.getOrgIds());
        map.put("key",requestDTO.getKey());
        map.put("orgId",requestDTO.getBody().getOrgId());
        return baseMapper.selectUserNotInOrg(page,map);
    }

    @Override
    public UserVo selectUserById(RequestDTO<User> requestDTO) throws Exception {
        Map<String,Object> map = new HashMap<>(10);
        map.put("id",requestDTO.getId());
        UserVo userVo = baseMapper.selectUserById(map);
        map.put("orgIds",requestDTO.getOrgIds());
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",requestDTO.getId());
        List<UserRole> list = userRoleMapper.selectList(wrapper);
        List<Integer> roleIds = new ArrayList<>(10);
        for(int i = 0;i < list.size();i++) {
            roleIds.add(list.get(i).getRoleId());
        }
        userVo.setRoleIds(roleIds);
        userVo.setUserRole(roleMapper.selectRoleNameByUserId(map));
        return userVo;
    }

    @Override
    public boolean changePWD(RequestDTO<UserVo> requestDTO) throws Exception {
        Map<String,Object> map = new HashMap<>(10);
        map.put("code",requestDTO.getBody().getCode());
        map.put("password",passwordEncoder.encode(requestDTO.getBody().getPassword()));
        User user = baseMapper.checkUser(map);
        if(!passwordEncoder.matches(requestDTO.getBody().getOldPassword(),user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        return retBool(this.baseMapper.changePWD(map));
    }

    @Override
    public List<User> getListUserByIds(String[] ids) {
        return this.baseMapper.getListUserByIds(ids);
    }
}
