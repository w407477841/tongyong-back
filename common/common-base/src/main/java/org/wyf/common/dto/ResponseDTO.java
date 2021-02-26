package org.wyf.common.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
@Data
@Accessors(chain=true)
public class ResponseDTO {



    private String msg;

    private Integer code;

    private Object data;





    /**
     * 返回列表数据
     * @param code
     * @param msg
     * @param list
     * @param <T>
     * @return
     */
    public static <T>ResponseDTO list(Integer code ,String msg,List<T> list ){
        return new ResponseDTO().setCode(code).setMsg(msg).setData(list);
    }

    public static <T>ResponseDTO obj(Integer code ,String msg,T obj ){
        return new ResponseDTO().setCode(code).setMsg(msg).setData(obj);
    }

    public static <T>ResponseDTO page(Integer code ,String msg,Page<T> page){
        return new ResponseDTO().setCode(code).setMsg(msg).setData(new RPage<T>().setRecords(page.getRecords()).setTotal(page.getTotal()));
    }



}
@Data
@Accessors(chain = true)
class RPage<T>{

    List<T> records;
    Long total;


}