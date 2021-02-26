package org.wyf.aop;

import cn.hutool.json.JSONUtil;
import org.wyf.common.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
@ControllerAdvice(basePackages = "org.wyf.api")
@Slf4j
public class LogResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        log.info("[{}]返回值: {}",Const.REQUEST_SERIAL_NUMBER.get(),JSONUtil.toJsonStr(o));
        Const.REQUEST_SERIAL_NUMBER.remove();
        Const.orgId.remove();
        Const.orgIds.remove();
        Const.token.remove();
        Const.CLIENT.remove();
        Const.LOGIN_TYPE.remove();
        return o;
    }
}
