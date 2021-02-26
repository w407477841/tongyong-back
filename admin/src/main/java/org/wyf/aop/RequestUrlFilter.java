package org.wyf.aop;

import cn.hutool.core.util.RandomUtil;
import org.wyf.common.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
@Slf4j
public class RequestUrlFilter extends GenericFilterBean {

    private final String contextPath;

    public RequestUrlFilter(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest hsr = (HttpServletRequest) servletRequest;
        String uri =  hsr.getRequestURI();
        if((contextPath+"/error").equals(uri)){

        }else{
            String serialNumber = RandomUtil.randomStringUpper(32);
            Const.REQUEST_SERIAL_NUMBER.set(serialNumber);
            log.info("[{}]请求URI: {}",serialNumber,uri);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
