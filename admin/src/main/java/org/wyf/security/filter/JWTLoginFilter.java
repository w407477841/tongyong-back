package org.wyf.security.filter;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.wyf.common.constant.Const;
import org.wyf.security.SecurityConfig;
import org.wyf.security.dto.UserDTO;
import org.wyf.security.enums.LoginType;
import org.wyf.security.service.SecurityService;
import org.wyf.security.token.BaseAuthenticationToken;
import org.wyf.security.token.MyUsernameAuthenticationToken;
import org.wyf.system.UserVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.wyf.system.UserVO;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.wyf.common.constant.ResultCodeEnum.NO_ORG;

/**
 * 验证用户名密码正确后，生成一个token，并将token返回给客户端 
 * 该类继承自UsernamePasswordAuthenticationFilter，重写了其中的2个方法 
 * attemptAuthentication ：接收并解析用户凭证。 
 * successfulAuthentication ：用户成功登录后，这个方法会被调用，我们在这个方法里生成token。 
 * 
 * 会拦截
 *  POST /login/
 *  requestBody:{
 *  username:""
 *  password:""
 *  
 *  }
 * 
 */
@Slf4j
public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {
	private AuthenticationManager  authenticationManager;
	private SecurityService userService;
	public JWTLoginFilter(AuthenticationManager authenticationManager) {
		super();

		this.authenticationManager = authenticationManager;
	}
	public JWTLoginFilter(AuthenticationManager authenticationManager,SecurityService  userService) {
		super();
		this.authenticationManager = authenticationManager;
		this.userService  =  userService;
	}



	/**
	 * 接收并解析用户凭证
	 * @param req
	 * @param res
	 * @return
	 * @throws AuthenticationException
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req,
			HttpServletResponse res) throws AuthenticationException {
			//获取请求中的用户名/密码
			try {
				
				UserDTO user = new ObjectMapper()
				            .readValue(req.getInputStream(), UserDTO.class);
				//验证 clientId 和 clientSecrit
				String clientId =  user.getClientId();
				String clientSecrit = user.getClientSecret();
				if(!SecurityConfig.CLIENT_ID.equals(clientId)||!SecurityConfig.CLIENT_SECRET.equals(clientSecrit)){
						return null;
				}
				Class< ? extends BaseAuthenticationToken> authenticationTokenClass = LoginType.getAuthenticationToken(user.getLoginType());
				if(authenticationTokenClass == null){
					return null;
				}
				Authentication auth=authenticationManager.authenticate(
						//对应UsernameAuthenticationProvider
						ReflectUtil.newInstance(authenticationTokenClass,
								user,
								user.getPassword()));
				return auth;
			} catch (IOException e) {
			 	e.printStackTrace();
			 	return null;
			}
	           
	          
	          
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
		log.error("异常{}",failed.getMessage(),failed);
		super.unsuccessfulAuthentication(request, response, failed);
	}

	// 用户成功登录后，这个方法会被调用，我们在这个方法里生成token
	//返回给前台的数据
	@Override
	protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
//		UserDTO userObj = new ObjectMapper()
//				.readValue(req.getInputStream(), UserDTO.class);
		//验证 clientId 和 clientSecrit

		Claims claims =new  DefaultClaims();
		Long lasttime=System.currentTimeMillis() + JWTAuthenticationFilter.JWT_EXP;
		UserDTO userObj = (UserDTO) auth.getPrincipal();
		String username = userObj.getUsername();
		claims.put("client",userObj.getClientId());
		claims.put("loginType",userObj.getLoginType());
		//持有者,通过账号登录的是账号名，通过手机登录的是手机号
		claims.put("sub",username);
		//过期时间
		claims.put("exp", new Date(lasttime));
		//权限
		claims.put("auths",null);
		String token = Jwts.builder()
	               .setClaims(claims)
	                .setExpiration(new Date(lasttime))  
	                .signWith(SignatureAlgorithm.HS512, JWTAuthenticationFilter.JWT_SECRET)
	                .compact(); 
		 
		 //token  加入    缓存key= xywg:user:token  
		 //权限   加入缓存  key  =xywg:user:permission
		

		Map<String,Object> resultJson = new HashMap<>(16);

		// 解决同时登录问题
		Const.token.set(token.split("\\.")[2]);

		UserVO user = 	userService.getUser(username,Const.USER_KEY_PREFIX);
		//删除之前的集团缓存，防止切换集团后无反应
		//userService.removeOrgids(user.getId());
		user.setPassword(null);
		if(user.getGroups()==null||user.getGroups().size()==0){
			// 没有集团
			resultJson.put("code",NO_ORG.code());
			resultJson.put("message",NO_ORG.msg() );
			if(user.getGroups().size()==1){
				userService.updateOrgId(Const.token.get(),user.getGroups().get(0).getId());
			}
		} else{
			resultJson.put("user",user);
			resultJson.put("code",200);
			resultJson.put("message","登录成功" );
			resultJson.put("auth",JWTAuthenticationFilter.JTW_TOKEN_HEAD + token);
		}


		res.setContentType("application/json;charset=UTF-8");
		 	res.getWriter().write(
					JSONUtil.toJsonStr(resultJson)
		 	);
	   res.addHeader(JWTAuthenticationFilter.JWT_HEADER, JWTAuthenticationFilter.JTW_TOKEN_HEAD + token);
	}
	
	
	private boolean checkUserAgent(String ua){
		String[] agent = { "Android", "iPhone", "iPod","iPad", "Windows Phone", "MQQBrowser" }; //定义移动端请求的所有可能类型
		boolean flag = false;
		if (!ua.contains("Windows NT") || (ua.contains("Windows NT") && ua.contains("compatible; MSIE 9.0;"))) {
		// 排除 苹果桌面系统
		if (!ua.contains("Windows NT") && !ua.contains("Macintosh")) {
			for (String item : agent) {
				if (ua.contains(item)) {
					flag = true;
					break;
				}
			}
		}
		}
		return flag;
		}

}
