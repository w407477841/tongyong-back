package org.wyf.aop;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import org.wyf.common.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;
@Slf4j
@ControllerAdvice(basePackages = {"org.wyf.api"})
public class LogRequestAdvice implements RequestBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        //判断是否有此注解
        boolean b = methodParameter.getParameterAnnotation(RequestBody.class) != null;
        //只有为true时才会执行afterBodyRead

        return b;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        return httpInputMessage;
    }

    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {

        log.info("[{}]请求头: {}",Const.REQUEST_SERIAL_NUMBER.get(),JSONUtil.toJsonStr(httpInputMessage.getHeaders()));
        log.info("[{}]请求参数: {}",Const.REQUEST_SERIAL_NUMBER.get(),JSONUtil.toJsonStr(o));



        return o;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        log.info("[{}]请求头: {}",Const.REQUEST_SERIAL_NUMBER.get(),JSONUtil.toJsonStr(httpInputMessage.getHeaders()));
        log.info("[{}]请求参数: ",Const.REQUEST_SERIAL_NUMBER.get());
        return null;
    }
}
