package org.wyf.api.test;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wyf.common.controller.BaseController;
import org.wyf.test.entity.Test;
import org.wyf.test.service.ITestService;

import javax.annotation.security.RolesAllowed;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wyf
 * @since 2019-11-13
 */
@RestController
@RequestMapping("/test/test")
public class TestController extends BaseController<Test,ITestService> {


@Override
public String insertRole() {
        return "test:test:insert";
        }

@Override
public String updateRole() {
        return "test:test:update";
        }

@Override
public String deleteRole() {
        return "test:test:delete";
        }

@Override
public String viewRole() {
        return "test:test:view";
        }


        @PreAuthorize("hasAuthority('test:test:abc')")
        @PostMapping("test")
        public String test(){
        return "ok";
        }

        @PostMapping("test1")
        public String test1(){
        hasPermission("test:test:abc");
                return "ok";
        }

}

