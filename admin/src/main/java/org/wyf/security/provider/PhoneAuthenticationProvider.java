package org.wyf.security.provider;

import org.wyf.cache.CacheConst;
import org.wyf.cache.RedisUtil;
import org.wyf.common.constant.Const;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.ResultDTO;
import org.wyf.security.dto.UserDTO;
import org.wyf.security.service.SecurityService;
import org.wyf.security.token.MyUsernameAuthenticationToken;
import org.wyf.security.token.PhoneCodeAuthenticationToken;
import org.wyf.system.service.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.wyf.cache.CacheConst;
import org.wyf.cache.RedisUtil;


/**
 * 自定义用户名密码认证
 * 与MyUsernameAuthenticationToken绑定
 */
@Component
public class PhoneAuthenticationProvider implements AuthenticationProvider {

	 @Autowired
			/**检测用户名*/
			 SecurityService securityService;

			 @Autowired
             RedisUtil redisUtil;


	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		
		String phone=authentication.getName();
		String code =(String) authentication.getCredentials();
		UserDTO user = (UserDTO) securityService.loadUserByPhone(phone);
		if(user ==null){
			Const.UNAUTHORIZED.set(ResultCodeEnum.NOUSER);
			throw new UsernameNotFoundException("用户名不存在");
		}
		if(!redisUtil.exists(CacheConst.VALID_KEY+"login:"+phone)){
			Const.UNAUTHORIZED.set(ResultCodeEnum.NO_VALID_CODE);
			throw new UsernameNotFoundException("请先获取验证码");
		}
		String cacheCode = (String)redisUtil.get(CacheConst.VALID_KEY+"login:"+phone);
		// 删除验证码
		redisUtil.remove(CacheConst.VALID_KEY+"login:"+user.getPhone());
		if(!cacheCode.equals(code)){
			Const.UNAUTHORIZED.set(ResultCodeEnum.VALID_CODE_ERROR);
			 throw new UsernameNotFoundException("验证码错误");
		}
		return new PhoneCodeAuthenticationToken(user, code);

		
		
	}

	//与MyUsernameAuthenticationToken绑定。
	
	@Override
	public boolean supports(Class<?> authentication) {
		return PhoneCodeAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
