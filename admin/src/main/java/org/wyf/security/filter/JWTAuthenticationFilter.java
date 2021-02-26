package org.wyf.security.filter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import org.wyf.common.constant.Const;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.security.service.SecurityService;
import org.wyf.system.ConstSystem;
import org.wyf.system.UserVO;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.wyf.system.ConstSystem;
import org.wyf.system.UserVO;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;


/**
* @author: wangyifei
* Description: 拦截需要权限验证的所有请求 判断 header 中 或 参数中是否有JWT
* Date: 13:48 2018/9/8
*/
@SuppressWarnings("all")
public class JWTAuthenticationFilter extends OncePerRequestFilter {
	/**
	 * jwt.header = Authorization jwt.secret = zyiot jwt.expiration = 604800
	 * jwt.tokenHead = "Bearer "
	 */


	public static String   JWT_SECRET = "honghu" ;

	public static long  JWT_EXP = 7200000 ;

	public static String JWT_HEADER = "Authorization";

	public static String JTW_TOKEN_HEAD = "Bearer ";


	private SecurityService   securityService;
	

	public JWTAuthenticationFilter(SecurityService securityService) {
		super();
		this.securityService = securityService;

	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
	    // 请求投中获取 token
		String headertoken = request.getHeader(JWT_HEADER);

		if (StrUtil.isBlank(headertoken)) {
		//请求参数中取
			String token = request.getParameter("auth");
			if(StrUtil.isBlank(token) ){
				Const.UNAUTHORIZED.set(ResultCodeEnum.NOT_LOGIN);
				chain.doFilter(request, response);
				return;
			}else{
				headertoken = token;
			}

		}else{
			headertoken =	headertoken.replace(JTW_TOKEN_HEAD, "");
		}
		// 将认证信息放入security上下文
		SecurityContextHolder.getContext().setAuthentication(getAuthentication(request,headertoken ));
		chain.doFilter(request, response);
	} 

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, String token) {
		//System.out.println("==token=="+token);
	// 获取IP地址
	//	System.out.println("x-forwarded-for:" + request.getHeader("x-forwarded-for"));
	//	System.out.println("remoteAddr:" + request.getRemoteAddr());
		if (token != null) {
			// 解析 Authorization 得到用户名.
			Claims claims = null;

			try {
				//解析获得 权鉴
				claims = Jwts.parser().setSigningKey(JWT_SECRET)
						.parseClaimsJws(token).getBody();

			} catch (ExpiredJwtException e) {
				e.printStackTrace();
			} catch (UnsupportedJwtException e) {
				e.printStackTrace();
			} catch (MalformedJwtException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} finally {
				if (claims == null) {
					// ThreadLocalExceptionMessage.push("登录过期", 403);
					// throw new RuntimeException("登录过期");
					Const.UNAUTHORIZED.set(ResultCodeEnum.TOEKN_ERROR);
					return null;

				}
			}
			String clientId = claims.get("client",String.class);
			String loginType = claims.get("loginType",String.class);


			Const.CLIENT.set(clientId);
			Const.LOGIN_TYPE.set(loginType);

			String user = claims.getSubject();
			//将token保存在当前线程中
			Const.token.set(token.split("\\.")[2]);
			//将用户保存在当前线程中
			UserVO curUser= securityService.getUser(user, Const.USER_KEY_PREFIX);

			ConstSystem.currUser.set(curUser);
			if(ConstSystem.currUser.get().getGroups().size()==1){
				//只有一个 组织
				Const.orgId.set(securityService.getOrgId(Const.token.get()));
				Const.orgIds.set(securityService.getOrgids(ConstSystem.currUser.get().getId()));

			}else if(ConstSystem.currUser.get().getGroups().size()==0){
				// 应该是微信小程序注册的
				Const.orgId.set(1);
				Const.orgIds.set(CollectionUtil.newArrayList(1));

			}else{
				//存在多个组织时,需要调用下system/organization/chooseOrg
				//将组织保存在当前线程中
				Const.orgId.set(securityService.getOrgId(Const.token.get()));
				Const.orgIds.set(securityService.getOrgids(ConstSystem.currUser.get().getId()));
			}


			return new UsernamePasswordAuthenticationToken(user, null,securityService.loadGrantedAuthorityByUser(user,Const.PERMISSION_KEY_PREFIX) );
		}
		Const.UNAUTHORIZED.set(ResultCodeEnum.TOEKN_ERROR);
		return null;
	}

}
