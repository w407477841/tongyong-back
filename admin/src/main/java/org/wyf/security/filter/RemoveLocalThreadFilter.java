package org.wyf.security.filter;

import cn.hutool.json.JSONUtil;
import org.wyf.common.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author ：WANGYIFEI
 * @date ：Created in 2020/11/2 10:58
 * @description：删除localthread，防止内存泄漏
 * @modified By：
 * @version: 1.0.0
 */
@Slf4j
public class RemoveLocalThreadFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        Const.UNAUTHORIZED.remove();
        log.info("[{}]移除security异常，防止内存泄漏",Const.REQUEST_SERIAL_NUMBER.get());
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
