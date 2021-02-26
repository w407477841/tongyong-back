package org.wyf.security.exception.custom;

import cn.hutool.json.JSONUtil;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ：WANGYIFEI
 * @date ：Created in 2020/11/2 14:19
 * @description：授权异常
 * @modified By：
 * @version: 1.0.0
 */
@Component
@Slf4j
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(200);
        ResultDTO result = ResultDTO.factory(ResultCodeEnum.NO_PERMISSION);
        log.info("无权限[{}]",result);
        response.setStatus(200);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(result));

    }
}
