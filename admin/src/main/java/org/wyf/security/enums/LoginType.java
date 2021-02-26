package org.wyf.security.enums;

import org.wyf.security.token.BaseAuthenticationToken;
import org.wyf.security.token.MyUsernameAuthenticationToken;
import org.wyf.security.token.PhoneCodeAuthenticationToken;
import org.wyf.security.token.MyUsernameAuthenticationToken;
import org.wyf.security.token.PhoneCodeAuthenticationToken;

public enum LoginType {
	//手机登录
	PHONE(PhoneCodeAuthenticationToken.class),
	//用户名登录
	USERNAME(MyUsernameAuthenticationToken.class);
	
	private  Class< ? extends BaseAuthenticationToken>  clazz;

	private LoginType(Class< ? extends BaseAuthenticationToken> clazz) {
		this.clazz = clazz;
	}


	public static Class< ? extends BaseAuthenticationToken>  getAuthenticationToken(String name){

			for( LoginType loginType: LoginType.values()){
				if(loginType.name().equals(name)){
					return loginType.clazz;
				}
			}
			return null;
	}
	
}
