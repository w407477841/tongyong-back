package org.wyf.security;

import org.springframework.beans.factory.annotation.Value;
import org.wyf.aop.RequestUrlFilter;
import org.wyf.security.exception.custom.MyAccessDeniedHandler;
import org.wyf.security.exception.custom.MyAuthenticationEntryPoint;
import org.wyf.security.filter.JWTAuthenticationFilter;
import org.wyf.security.filter.JWTLoginFilter;
import org.wyf.security.filter.RemoveLocalThreadFilter;
import org.wyf.security.provider.PhoneAuthenticationProvider;
import org.wyf.security.provider.UsernameAuthenticationProvider;
import org.wyf.security.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.wyf.security.exception.custom.MyAccessDeniedHandler;
import org.wyf.security.exception.custom.MyAuthenticationEntryPoint;
import org.wyf.security.filter.JWTAuthenticationFilter;
import org.wyf.security.filter.JWTLoginFilter;
import org.wyf.security.filter.RemoveLocalThreadFilter;
import org.wyf.security.provider.PhoneAuthenticationProvider;
import org.wyf.security.provider.UsernameAuthenticationProvider;
import org.wyf.security.service.SecurityService;


/**
 * 
 * 认证配置
 *
 */
@Slf4j
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity(debug = false)
@Order(value = SecurityProperties.BASIC_AUTH_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	public static String CLIENT_ID = "abcdef";
	public static String CLIENT_SECRET = "123456";
	public static String[] SUPPORT_LOGIN_TYPE={"USERNAME","PHONE"};
	@Autowired
	private SecurityService securityService;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UsernameAuthenticationProvider usernameAuthenticationProvider;
	@Autowired
	private MyAuthenticationEntryPoint myAuthenticationEntryPoint;
	@Autowired
	private MyAccessDeniedHandler myAccessDeniedHandler;

	@Autowired
	private PhoneAuthenticationProvider phoneAuthenticationProvider;
	@Value("${server.servlet.context-path}")
	private String contextPath;


	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth
		.authenticationProvider(usernameAuthenticationProvider)
		.authenticationProvider(phoneAuthenticationProvider)
		.userDetailsService(securityService)
		.passwordEncoder(bCryptPasswordEncoder)
		;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {

		web.ignoring().antMatchers(HttpMethod.POST, "/users").antMatchers("/", "/auth/**", "/resources/**",
				"/static/**", "/public/**", "/webui/**", "/h2-console/**", "/configuration/**", "/swagger-ui/**",
				"/swagger-resources/**", "/api-docs", "/api-docs/**", "/v2/api-docs/**", "/*.html", "/**/*.html",
				"/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.gif", "/**/*.svg", "/**/*.ico", "/**/*.ttf",
				"/**/*.woff"
		)
				// 塔吊数据
				//.antMatchers("/craneData/api/**")
				// word导出
				.antMatchers("/word/download")
				.antMatchers("/craneToPdf/report")
		;


	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {

	JWTLoginFilter loginFilter=	new JWTLoginFilter( authenticationManager(),
				securityService);
		httpSecurity
				.cors().and()
				// 由于使用的是JWT，我们这里不需要csrf
				.csrf().disable()
				// 自定义认证异常，授权异常
				 .exceptionHandling()
				.authenticationEntryPoint(myAuthenticationEntryPoint)
				.accessDeniedHandler(myAccessDeniedHandler)
				.and()
//				.formLogin()
//				.loginProcessingUrl("/wx/login")
//				.and()
				// 原本403异常会被处理掉，现在会抛出异常。并且只能在为进入controler的全局异常处理捕获
				// 基于token，所以不需要session
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				// 允许对于网站静态资源的无授权访问
				.antMatchers(HttpMethod.GET, "/", "/*.html", "/favicon.ico", "/**/*.html", "/**/*.css", "/**/*.js")
				// 任务
				.permitAll().antMatchers(HttpMethod.GET, "/task/**").permitAll()
				// 上传
				.antMatchers(HttpMethod.GET, "/upload/image/**").permitAll()
				.antMatchers(HttpMethod.POST, "/user/signup").permitAll()



				.antMatchers(HttpMethod.POST,"/login").permitAll()
				.antMatchers(HttpMethod.GET,"/auth").permitAll()
				// validPhone
				.antMatchers(HttpMethod.POST, "/wechat/smallapplet/validPhone").permitAll()
				// 小程序 发注册验证短信
				.antMatchers(HttpMethod.POST, "/wechat/smallapplet/sendValidateCode").permitAll()
				// 小程序 注册
				.antMatchers(HttpMethod.POST, "/wechat/smallapplet/register").permitAll()
				// 小程序 登录
				.antMatchers(HttpMethod.POST, "/wechat/smallapplet/login").permitAll()
				// 支付回调
				.antMatchers( "/wechat/smallapplet/payNotify").permitAll()

				.antMatchers( "/wechat/smallapplet/balanceNotify").permitAll()

				// 换取openid
				.antMatchers(HttpMethod.POST, "/wechat/smallapplet/jscode2session").permitAll()

				// 换取设备号
				.antMatchers(HttpMethod.POST, "/wechat/smallapplet/getDeviceNo").permitAll()
				// 查询充电桩状态
				.antMatchers(HttpMethod.POST, "/wechat/smallapplet/getPileStatus").permitAll()
				// 查询版本号
				.antMatchers(HttpMethod.POST, "/wechat/smallapplet/getVersion").permitAll()

				// websocket
				.antMatchers(HttpMethod.GET, "/hello/**").permitAll()
				.antMatchers(HttpMethod.GET, "/send").permitAll()
				.antMatchers(HttpMethod.GET, "/send/**").permitAll()
				.antMatchers(HttpMethod.GET,"/actuator/**").permitAll()
				.antMatchers(HttpMethod.POST,"/process/**").permitAll()
				// .antMatchers("/other","/other/**").hasRole("ADMIN")
				// 除上面外的所有请求全部需要鉴权认证
				.anyRequest().authenticated().and()
				.addFilter(loginFilter)
				.addFilterBefore(new RequestUrlFilter(contextPath),JWTLoginFilter.class)
				.addFilterAfter(new JWTAuthenticationFilter(securityService), JWTLoginFilter.class)
				.addFilterAfter(new RemoveLocalThreadFilter(),FilterSecurityInterceptor.class)
		;


		// 添加JWT filter
		// httpSecurity.addFilterBefore(authenticationTokenFilterBean(),
		// UsernamePasswordAuthenticationFilter.class);
		// 禁用缓存
		/// httpSecurity.headers().cacheControl();

	}




}
