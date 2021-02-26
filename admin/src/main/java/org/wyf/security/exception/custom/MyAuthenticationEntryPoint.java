package org.wyf.security.exception.custom;

import cn.hutool.json.JSONUtil;
import org.wyf.common.constant.Const;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ：WANGYIFEI
 * @date ：Created in 2020/10/30 16:17
 * @description：自定义认证异常处理
 * @modified By：
 * @version: 1.0.0
 */
@Component
@Slf4j
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        ResultCodeEnum resultCodeEnum = Const.UNAUTHORIZED.get();
        if(resultCodeEnum == null){
            resultCodeEnum = ResultCodeEnum.NOUSER;
        }
        ResultDTO result = ResultDTO.factory(resultCodeEnum);
        log.info("认证异常[{}]",result);
        httpServletResponse.setStatus(200);
        httpServletResponse.setHeader("Content-Type","application/json;charset=UTF-8");
        httpServletResponse.getWriter().write(JSONUtil.toJsonStr(result));
    }
}



/**
 @Component
 public class MyAccessDeniedHandler implements AccessDeniedHandler {
 @Override
 public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
 response.setStatus(403);
 response.getWriter().write("Forbidden:" + accessDeniedException.getMessage());
 }
 }


 */