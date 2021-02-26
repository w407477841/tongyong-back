package org.wyf.api.aftersale;


import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.wyf.common.controller.BaseController;
import org.wyf.aftersale.entity.AsIdea;
import org.wyf.aftersale.service.IAsIdeaService;
/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yaoy
 * @since 2019-12-12
 */
@RestController
@RequestMapping("/aftersale/as-idea")
public class AsIdeaController extends BaseController<AsIdea,IAsIdeaService> {


@Override
public String insertRole() {
        return "aftersale:asidea:insert";
        }

@Override
public String updateRole() {
        return "aftersale:asidea:update";
        }

@Override
public String deleteRole() {
        return "aftersale:asidea:delete";
        }

@Override
public String viewRole() {
        return "aftersale:asidea:view";
        }

}

