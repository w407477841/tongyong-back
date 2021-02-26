package org.wyf.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.wyf.cache.CacheConst;
import org.wyf.cache.RedisUtil;
import org.wyf.common.constant.Const;
import org.wyf.security.factory.UserFactory;
import org.wyf.security.filter.JWTAuthenticationFilter;
import org.wyf.system.UserVO;
import org.wyf.system.mapper.UserMapper;
import org.wyf.system.model.Operation;
import org.wyf.system.model.Organization;
import org.wyf.system.model.User;
import org.wyf.system.service.service.IOperationService;
import org.wyf.system.service.service.IOrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.wyf.cache.CacheConst;
import org.wyf.cache.RedisUtil;
import org.wyf.security.factory.UserFactory;
import org.wyf.security.filter.JWTAuthenticationFilter;
import org.wyf.system.UserVO;
import org.wyf.system.model.Operation;
import org.wyf.system.model.Organization;
import org.wyf.system.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
* @author: wangyifei
* Description: 权限缓存10分钟，
 * 用户缓存10分钟，
 * token对应组织缓存 jwtProperties 配置时间
 * token对应的组织树缓存jwtProperties 配置时间
* Date: 15:23 2018/9/4
*/
@Service
@Slf4j
public class SecurityServiceImpl implements SecurityService {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private IOrganizationService organizationService;

	@Autowired
	private IOperationService operationService;
	


	@Autowired
    RedisUtil redisUtil;
	
	/**
	 * 查询用户
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		QueryWrapper<User> wrapper  =new QueryWrapper<>();
 		wrapper.eq("code",username );
		User user = userMapper.selectOne(wrapper);
		return UserFactory.getUserDTO(user);
	}

	@Override
	public UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException {
		QueryWrapper<User> wrapper  =new QueryWrapper<>();
		wrapper.eq("phone",phone );
		User user = userMapper.selectOne(wrapper);
		return UserFactory.getUserDTO(user);
	}

	/**
	 * 从缓存中取 该用户的权限
	 */
	@Override
	public Collection<? extends GrantedAuthority> loadGrantedAuthorityByUser(String username,String keyPrefix) {


		String key  = CacheConst.REDIS_HEADER+":"+Const.token.get()+":auths";

		if(!redisUtil.exists(key)){
			List<SimpleGrantedAuthority>  authoritys = new ArrayList<>();

			List<Operation>  ops =  operationService.getOperations();

			for(Operation  operation:ops){
				SimpleGrantedAuthority  authority =new SimpleGrantedAuthority("ROLE_"+operation.getPermission());
				authoritys.add(authority);
			}
			redisUtil.set(key,authoritys,10L);
			return authoritys;
			}
		else{
			return (Collection<? extends GrantedAuthority>) redisUtil.get(key);
		}
	}


	@Override

	public UserVO getUser(String username, String keyPrefix) {

		String key = CacheConst.REDIS_HEADER+":"+Const.token.get()+":user"    ;

		//System.out.println("user  key="+key);

		if(redisUtil.exists(key)){
			return (UserVO)redisUtil.get(key);
		}else{
			QueryWrapper<User>  wrapper  =new QueryWrapper<>();
			wrapper.eq("code",username );
			User user = userMapper.selectOne(wrapper);
			UserVO userVo = UserVO.factory(user);
			//查询  所有
			List<Organization> orgs= organizationService.getByUserId(userVo.getId());
			userVo.setGroups(orgs);
			redisUtil.set(key,userVo,10L);
			return userVo;

		}



	}

	@Override
	public Integer updateOrgId(String token,Integer orgId) {
	String key = CacheConst.REDIS_HEADER+":"+token+":orgid";
		   redisUtil.set(key,orgId,JWTAuthenticationFilter.JWT_EXP /1000/60);
		return orgId;
	}

	@Override
	public Integer getOrgId(String token) {
		String key = CacheConst.REDIS_HEADER+":"+token+":orgid";
		if(redisUtil.exists(key)){
			return (Integer) redisUtil.get(key);
		}

		return null;
	}

	@Override
	public List<Integer> getOrgids(Integer userId) {
		if(userId == null) {
			//只可能是 多部门时，选择集团
			return null;
		}
		String key = CacheConst.REDIS_HEADER+":"+Const.token.get()+":orgids";
			if(redisUtil.exists(key)){
				return (List<Integer>) redisUtil.get(key);
			}
			return null;

	}

	@Override
	public List<Integer> updateOrgids(Integer userId) {
		if(userId == null) {
			//只可能是 多部门时，选择集团
			return null;
		}
		String key = CacheConst.REDIS_HEADER+":"+Const.token.get()+":orgids";
			List<Integer> list =  organizationService.getOrgsByParent(getOrgId(Const.token.get()));
			redisUtil.set(key,list,JWTAuthenticationFilter.JWT_EXP/1000/60);
			return	list;
		}


	@Override
	public void removeOrgids(Integer userId) {
		String key = CacheConst.REDIS_HEADER+":"+Const.token.get()+":orgids";
		if(redisUtil.exists(key)){
			redisUtil.remove(key);
		}
		}

}
