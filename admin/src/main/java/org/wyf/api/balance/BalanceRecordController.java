package org.wyf.api.balance;


import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wyf.common.controller.BaseController;
import org.wyf.balance.entity.BalanceRecord;
import org.wyf.balance.service.IBalanceRecordService;
import org.wyf.balance.entity.BalanceRecord;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wangyifei
 * @since 2019-12-23
 */
@RestController
@RequestMapping("/balance/balance-record")
public class BalanceRecordController extends BaseController<BalanceRecord,IBalanceRecordService> {


@Override
public String insertRole() {
        return "balance:balancerecord:insert";
        }

@Override
public String updateRole() {
        return "balance:balancerecord:update";
        }

@Override
public String deleteRole() {
        return "balance:balancerecord:delete";
        }

@Override
public String viewRole() {
        return "balance:balancerecord:view";
        }

}

