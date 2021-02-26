package org.wyf.common.controller;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
//import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wyf.common.constant.OperationEnum;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.DataDTO;
import org.wyf.common.dto.RequestDTO;
import org.wyf.common.dto.ResultDTO;
import org.wyf.common.exception.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.wyf.common.exception.ForbiddenException;


public abstract class BaseController<T, S extends IService<T>> {
    @Autowired
    public S service;

    /**
     * 新增权限    返回空为不拦截
     */
    public abstract String insertRole();

    /**
     * 修改权限    返回空为不拦截
     */
    public abstract String updateRole();

    /**
     * 删除权限    返回空为不拦截
     */
    public abstract String deleteRole();

    public abstract String viewRole();

    /**
     * 新增
     *
     * @param t
     * @return
     */
    @PostMapping("insert")
    public ResultDTO<T> insert(@RequestBody RequestDTO<T> t) {

        hasPermission(insertRole());


        try {
            if (service.save(t.getBody())) {
                return ResultDTO.resultFactory(OperationEnum.INSERT_SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultDTO.resultFactory(OperationEnum.INSERT_ERROR);
    }

    /**
     * 修改
     */
    @PostMapping("update")
    public ResultDTO<T> update(@RequestBody RequestDTO<T> t) {
        hasPermission(updateRole());
        try {
            if (service.updateById(t.getBody())) {
                return ResultDTO.resultFactory(OperationEnum.UPDATE_SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResultDTO.resultFactory(OperationEnum.UPDATE_ERROR);
    }

    /**
     * 删除
     *
     * @param t
     * @return
     */
    @PostMapping("delete")
    public ResultDTO<T> delete(@RequestBody RequestDTO<T> t) {
        hasPermission(deleteRole());
        try {
            if (service.removeById(t.getId())) {
                return ResultDTO.resultFactory(OperationEnum.DELETE_SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultDTO.resultFactory(OperationEnum.DELETE_ERROR);
    }

    /**
     * 批量删除
     *
     * @param t
     * @return
     */

    @PostMapping("deletes")
    public ResultDTO<T> deletes(@RequestBody RequestDTO<T> t) {
        hasPermission(deleteRole());
        try {
            if (service.removeByIds(t.getIds())) {
                return ResultDTO.resultFactory(OperationEnum.DELETE_SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultDTO.resultFactory(OperationEnum.DELETE_ERROR);
    }

    @PostMapping("get")
    public ResultDTO<T> get(@RequestBody RequestDTO<T> t) {
        hasPermission(viewRole());

        T result = service.getById(t.getId());

        return new ResultDTO<T>(true, result);
    }

    @PostMapping("selectList")
    public ResultDTO<DataDTO<List<T>>> selectList(@RequestBody RequestDTO<T> t) {
        hasPermission(viewRole());
        String tableName =  t.getBody().getClass().getAnnotation(TableName.class).value();
        String createUserName = StrUtil.format("(select name from t_sys_user where t_sys_user.id = {}.create_user ) as createUserName",tableName);
        Page<T> page = new Page<>(t.getPageNum(), t.getPageSize());
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*",createUserName);


        if(t.getColumns()!=null&&t.getColumns().size()>0&&StrUtil.isNotBlank(t.getKey())){
            // 组装 like 语句
            queryWrapper.and(tQueryWrapper -> {
                t.getColumns().forEach(item->{
                    tQueryWrapper.or().like(item,t.getKey());

                });
                return tQueryWrapper;
            });


        }
        queryWrapper.orderByDesc("create_time");

        List<T> ts = service.page(page,queryWrapper).getRecords();

        return new ResultDTO<DataDTO<List<T>>>(true, DataDTO.factory(ts, page.getTotal()));
    }

    @PostMapping("selectListNoPage")
    public ResultDTO<List<T>> selectListNoPage(@RequestBody RequestDTO<T> t) {
        hasPermission(viewRole());
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("is_del", 0);
        List<T> list = service.list(wrapper);
        return new ResultDTO<>(true, list);

    }


    protected void hasPermission(String permission) {
        //不设置认为不需要认证
        if (permission == null || "".equals(permission)) {
            return;
        }
        Collection<? extends GrantedAuthority> auths = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (GrantedAuthority auth : auths) {
            if (auth.getAuthority().replace("ROLE_", "").equals(permission)) {
                return;
            }
        }
        throw new ForbiddenException(ResultCodeEnum.NO_PERMISSION.msg());
    }


}
