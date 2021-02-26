package org.wyf.aop;

import cn.hutool.json.JSONUtil;
import org.wyf.common.constant.Const;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.ResultDTO;
import org.wyf.common.exception.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常拦截
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {
	
	@ExceptionHandler(value=ForbiddenException.class)
	@ResponseBody
    public ResultDTO<Object> handleException(ForbiddenException e) {
        log.info("[{}]无权限: hasPermission(\"x:x:x\");",Const.REQUEST_SERIAL_NUMBER.get());
        return ResultDTO.factory(ResultCodeEnum.NO_PERMISSION);
    }



    @ExceptionHandler(value=AccessDeniedException.class)
    @ResponseBody
    public ResultDTO<Object> handleException(AccessDeniedException e) {
        log.info("[{}]无权限:  @PreAuthorize(\"hasAuthority('x:x:x')\");",Const.REQUEST_SERIAL_NUMBER.get());
        return ResultDTO.factory(ResultCodeEnum.NO_PERMISSION);
    }


    @ExceptionHandler(value=HttpMessageNotReadableException.class)
    @ResponseBody
    public ResultDTO<Object> htpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("[{}]参数异常",Const.REQUEST_SERIAL_NUMBER.get(),e);
        return ResultDTO.factory(ResultCodeEnum.PARAMS_ERROR);
    }
    @ExceptionHandler(value=Exception.class)
    @ResponseBody
    public ResultDTO<Object> handleAllException(Exception e) {
	    e.printStackTrace();
        return ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
    }

	
}
