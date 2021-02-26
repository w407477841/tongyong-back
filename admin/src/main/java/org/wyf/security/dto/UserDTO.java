package org.wyf.security.dto;

import org.wyf.system.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.wyf.system.model.User;

import java.util.Collection;
import java.util.List;

public class UserDTO extends User implements UserDetails{
	private static final long serialVersionUID = 1L;
	
	private Collection<? extends GrantedAuthority> authorities;
	/**
	 * 账户已过期
	 */
	private boolean accountNonExpired ;
	/**
	 * 账户已被锁定
	 */
	private boolean accountNonLocked;
	/**
	 * 证书已过期
	 */
	private boolean credentialsNonExpired;
	/**
	 * 已激活
	 */
	private boolean isEnabled ;
	/**
	 *用户名
	 */
	private String username;



	/**
	 * 登录方式   USERNAME  PHONE
	 */
	private String loginType;

	private String clientId;

	private String clientSecret;

	/**
	 * 组织树
	 */
	private List<Integer> orgIds;
	
	
	public List<Integer> getOrgIds() {
		return orgIds;
	}
	public void setOrgIds(List<Integer> orgIds) {
		this.orgIds = orgIds;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}
	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}
	@Override
	public boolean isEnabled() {
		return isEnabled;
	}
	@Override
	public String getUsername() {
		return username;
	}
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

}
