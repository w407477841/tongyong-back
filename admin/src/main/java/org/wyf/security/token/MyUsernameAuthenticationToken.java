package org.wyf.security.token;

import org.wyf.security.dto.UserDTO;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.wyf.security.dto.UserDTO;

import java.util.Collection;

public class MyUsernameAuthenticationToken extends BaseAuthenticationToken{


	public MyUsernameAuthenticationToken(Collection<? extends GrantedAuthority> authorities, UserDTO principal, String credentials) {
		super(authorities, principal, credentials);
	}

	public MyUsernameAuthenticationToken(UserDTO principal, String credentials) {
		super(principal, credentials);
	}
}
