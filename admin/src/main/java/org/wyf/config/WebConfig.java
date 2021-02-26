package org.wyf.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author : wangyifei
 * Description
 * Date: Created in 14:40 2019/6/22
 * Modified By : wangyifei
 */
@Configuration
@Slf4j
public class WebConfig {

    /**
     * 配置 跨域相关
     * @return
     */
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //coresConfiguration.addAllowedOrigin("http://192.168.0.166:8080");//带token不能用*
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        //webocket需要设置
        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
    }

    /**
     * 跨域过滤器
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }


    //配置 密码器
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        log.info("密码器初始化完毕" );
        return new BCryptPasswordEncoder();
    }

}
