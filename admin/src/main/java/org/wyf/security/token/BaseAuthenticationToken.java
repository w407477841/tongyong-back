package org.wyf.security.token;

import org.wyf.security.dto.UserDTO;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.wyf.security.dto.UserDTO;

import java.util.Collection;

/**
 * @author ：WANGYIFEI
 * @date ：Created in 2020/11/2 10:38
 * @description：鉴权类的父类
 * @modified By：
 * @version: 1.0.0
 */
public class BaseAuthenticationToken extends AbstractAuthenticationToken {
    private final UserDTO principal;
    private String credentials;
    public BaseAuthenticationToken(Collection<? extends GrantedAuthority> authorities, UserDTO principal, String credentials) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true); // must use super, as we override
    }
    public BaseAuthenticationToken( UserDTO principal, String credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true); // must use super, as we override
    }




    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    /**
     * 设为已认证
     */
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }
    /** 清除凭证*/
    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }

}
