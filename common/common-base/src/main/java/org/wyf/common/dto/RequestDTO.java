package org.wyf.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class RequestDTO<T> {
    private T body;
    /** 搜索关键字 */
    private String key;
    /** 要like的字段，用在Basecontroller.selectList*/
    private List<String> columns;
    /** 分页大小 */
    private Integer pageSize;

    /** 当前页 */
    private Integer pageNum;
    /** 组织  */
    private Integer orgId;
    /** 组织树 */
    private List<Integer> orgIds;
    /** 删除 使用  */
    private Integer id;
    /** 批量删除使用 */
    private List<Integer> ids;
}
