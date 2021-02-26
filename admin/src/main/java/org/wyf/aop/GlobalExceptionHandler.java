package org.wyf.aop;

import org.wyf.common.constant.Const;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.ResultDTO;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author 全局异常处理
 * 
 * springsecurity 会出现  401 403 异常状态 .  此处将返回封装
 * 
 *
 */
@RestController
public class GlobalExceptionHandler implements ErrorController {



	@Override
	public String getErrorPath() {
		return "/error";
	}
	 @RequestMapping(value = "/error")
	    public Object error(HttpServletResponse resp, HttpServletRequest req) {
		 
			 if(resp.getStatus() == HttpStatus.BAD_REQUEST.value()){
				 resp.setStatus(200);
				 return ResultDTO.factory(ResultCodeEnum.EXCEPTION);
			 }else if(resp.getStatus() == HttpStatus.UNAUTHORIZED.value()){
				 resp.setStatus(200);
				 System.out.println(Const.UNAUTHORIZED.get().msg());
				 return  ResultDTO.factory(Const.UNAUTHORIZED.get());
			 }else if(resp.getStatus() == HttpStatus.FORBIDDEN.value()){
				 resp.setStatus(200);
				 return  ResultDTO.factory(ResultCodeEnum.NOT_LOGIN);
			 }else if(resp.getStatus() == HttpStatus.NOT_FOUND.value()){
				 resp.setStatus(200);
				 return  ResultDTO.factory(ResultCodeEnum.NOT_FOUND);
			 }else{
				 resp.setStatus(200);
				 return  ResultDTO.factory(ResultCodeEnum.SYS_ERROR);
			 }
			 
	    }

	
	
}
