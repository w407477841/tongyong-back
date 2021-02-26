package org.wyf.api.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.wyf.basic.entity.BasicPile;
import org.wyf.common.dto.DataDTO;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wyf.common.controller.BaseController;
import org.wyf.order.entity.OrderRecord;
import org.wyf.order.service.IOrderRecordService;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author cl
 * @since 2019-12-10
 */
@RestController
@RequestMapping("/order/order-record")
public class OrderRecordController extends BaseController<OrderRecord,IOrderRecordService> {


@Override
public String insertRole() {
        return "order:orderrecord:insert";
        }

@Override
public String updateRole() {
        return "order:orderrecord:update";
        }

@Override
public String deleteRole() {
        return "order:orderrecord:delete";
        }

@Override
public String viewRole() {
        return "order:orderrecord:view";
        }

        /**
         * 获取桩列表
         * @param requestDTO
         * @return
         */
        @PostMapping("selectOrderList")
        public ResultDTO<DataDTO<List<OrderRecord>>> selectPileList(@RequestBody RequestDTO<OrderRecord> requestDTO) {
                try {
                    Page<OrderRecord> page = new Page<>(requestDTO.getPageNum(),requestDTO.getPageSize());
                    List<OrderRecord> list = service.selectOrder(page,requestDTO);
                    return new ResultDTO<>(true,DataDTO.factory(list,page.getTotal()));
                }catch (Exception e) {
                        e.printStackTrace();
                }
                return new ResultDTO<>(false);
        }

}

