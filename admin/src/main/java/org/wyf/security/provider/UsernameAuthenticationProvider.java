package org.wyf.security.provider;

import org.wyf.common.constant.Const;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.security.dto.UserDTO;
import org.wyf.security.service.SecurityService;
import org.wyf.security.token.MyUsernameAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 自定义用户名密码认证
 * 与MyUsernameAuthenticationToken绑定
 */
@Component
public class UsernameAuthenticationProvider implements AuthenticationProvider {

	 @Autowired
			/**检测用户名*/
			 SecurityService securityService;

	    @Autowired
			/**密码生成器*/
	    BCryptPasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		String username=authentication.getName();
		String password =(String) authentication.getCredentials();
		UserDTO user = (UserDTO) securityService.loadUserByUsername(username);
		if(user ==null){
			Const.UNAUTHORIZED.set(ResultCodeEnum.NOUSER);
			throw new UsernameNotFoundException("用户名不存在");

		}
		if(passwordEncoder.matches(password,user.getPassword())){
			return new MyUsernameAuthenticationToken(user, password);
		}else{
			Const.UNAUTHORIZED.set(ResultCodeEnum.NOUSER);
			throw new BadCredentialsException("密码错误");	
		}
		
		
	}

	//与MyUsernameAuthenticationToken绑定。
	
	@Override
	public boolean supports(Class<?> authentication) {
		return MyUsernameAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
