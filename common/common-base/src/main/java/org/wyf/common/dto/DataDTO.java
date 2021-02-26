package org.wyf.common.dto;

import lombok.Data;

/**
 * 数据包装类
 * 
 * @author lw sakljkl
 *
 * @param <T>
 */
@Data
public class DataDTO<T> {
	/**
	 * 对象
	 */
	private T list;
	/**
	 * 条数
	 */
	private Long total;

	private DataDTO(T t) {
		this(t, 0L);
	}

	private DataDTO(T t, Long total) {
		this.list = t;
		this.total = total;
	}

	public static <T> DataDTO<T> factory(T t) {
		return new DataDTO<>(t, 0L);
	}

	public static <T> DataDTO<T> factory(T t, Long total) {
		return new DataDTO<>(t, total);
	}
}
