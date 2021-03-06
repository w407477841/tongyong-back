package org.wyf.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.constant.ResultCodeEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.constant.ResultCodeEnum;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultDTO<T> {
	/** 标志	 */
	private Boolean success;
	/** 状态码 */
	private Integer code;
	/** message	 */
	private String message;

	/** 数据*/
	private T data;

	public ResultDTO() {
	}

	public ResultDTO(Boolean success) {
		this(success, null);
	}

	public ResultDTO(String message) {
		this(true, null, message);
	}

	public ResultDTO(Boolean success, T data) {
		this(success, data, null);
	}

	public ResultDTO(Boolean success, T data, String message) {
		this(success, data, message, null);
	}

	public ResultDTO(Boolean success, T data, String message, Long total) {
		this.success = success;
		this.code = success ? ResultCodeEnum.SUCCESS.code() : ResultCodeEnum.FAIL.code();
		this.data = data;
		this.message = message == null ? success ? ResultCodeEnum.SUCCESS.msg() : ResultCodeEnum.FAIL.msg() : message;
		// this.total = total;
	}

	public ResultDTO(Boolean success, Integer code, String message,String data) {
		this.success = success;
		this.code = code;
		this.message = message;
	}

	/**
	 * 静态工厂
	 * 
	 * @param operation
	 * @return
	 */
	public static <T> ResultDTO<T> resultFactory(OperationEnum operation) {
		ResultDTO<T> dto = null;
		switch (operation) {
		case INSERT_SUCCESS:
		case UPDATE_SUCCESS:
		case SUBMIT_SUCCESS:
		case CANCEL_SUBMIT_SUCCESS:
		case DELETE_SUCCESS:
		case APPROVE_SUCCESS:
		case START_SUCCESS:
		case CLOSE_SUCCESS:
		case SEND_SUCCESS:
		case CHANGE_SUCCESS:
			dto = new ResultDTO<>(true, null, operation.getMessage());
			break;
		case INSERT_ERROR:
		case UPDATE_ERROR:
		case SUBMIT_ERROR:
		case DELETE_ERROR:
		case CANCEL_SUBMIT_ERROR:
		case CANCEL_SUBMIT_FAIL:
		case DELETE_FAIL:
		case APPROVE_ERROR:
		case START_FAIL:
		case CLOSE_FAIL:
		case SEND_ERROR:
		case CHANGE_FAIL:
			dto = new ResultDTO<>(false, null, operation.getMessage());
			break;
		default:
			new ResultDTO<>(true, null);
			break;
		}
		return dto;
	}

	/**
	 * 静态工厂
	 * 
	 * @param codeEnum 
	 * @return
	 */
	public static <T> ResultDTO<T> factory(ResultCodeEnum codeEnum) {
		
		ResultDTO<T> dto = new ResultDTO<T>();
		if(codeEnum==ResultCodeEnum.SUCCESS) {
			dto.setSuccess(true);
		}else {
			dto.setSuccess(false);
		}
		dto.setCode(codeEnum.code());
		dto.setMessage(codeEnum.msg());
		return dto;
	}
	/**
	 * 成功
	 * @param message
	 * @return
	 */
	public static <T> ResultDTO<T> success(String message) {
		ResultDTO<T> dto = new ResultDTO<T>(true,null,message);
		return dto;
	} 
	
	/**
	 * 失败
	 * @param message
	 * @return
	 */
	public static <T> ResultDTO<T> fail(String message) {
		ResultDTO<T> dto = new ResultDTO<T>(false,null,message);
		return dto;
	} 


}
