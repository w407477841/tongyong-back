package org.wyf.security.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import org.wyf.common.constant.Const;
import org.wyf.common.constant.ResultCodeEnum;
import org.wyf.common.dto.ResultDTO;
import org.wyf.security.dto.UserDTO;
import org.wyf.security.enums.LoginType;
import org.wyf.security.filter.JWTAuthenticationFilter;
import org.wyf.security.service.SecurityService;
import org.wyf.system.ConstSystem;
import org.wyf.system.UserVO;
import org.wyf.system.model.Organization;
import org.wyf.system.service.service.IOperationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.wyf.system.ConstSystem;
import org.wyf.system.UserVO;
import org.wyf.system.model.Organization;

import java.util.Date;
import java.util.List;

import static org.wyf.common.constant.ResultCodeEnum.NO_ORG;


/**
 * @author : wangyifei
 * Description
 * Date: Created in 16:52 2018/9/7
 * Modified By : wangyifei
 */
//@Controller
//@RequestMapping("")
@SuppressWarnings("all")
public class LoginController {
    @Autowired
    SecurityService securityService;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    IOperationService operationService;

@ApiOperation("登录")
@PostMapping("login")
@ResponseBody

public Object login(@RequestBody UserDTO user){
    //用户名
    String username = user.getUsername();
    //密码
    String password  = user.getPassword();

         user = (UserDTO) securityService.loadUserByUsername(username);
        if(user ==null){
            return ResultDTO.factory(ResultCodeEnum.NOUSER);
    //            throw new UsernameNotFoundException("用户名不存在");
        }
        if(passwordEncoder.matches(password,user.getPassword())){
            Claims claims =new DefaultClaims();
            Long lasttime=System.currentTimeMillis() + JWTAuthenticationFilter.JWT_EXP;
            //持有者
            claims.put("sub",username);
            //过期时间
            claims.put("exp", new Date(lasttime));
            //权限
            claims.put("auths",null);
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(new Date(lasttime))
                    .signWith(SignatureAlgorithm.HS512, JWTAuthenticationFilter.JWT_SECRET)
                    .compact();
            Const.token.set(token.split("\\.")[2]);
            UserVO userVO = securityService.getUser(username,Const.USER_KEY_PREFIX);
            //删除之前的集团缓存，防止切换集团后无反应
            //userService.removeOrgids(user.getId());
            if(userVO.getGroups()==null||userVO.getGroups().size()==0){
                // 没有集团
                return ResultDTO.factory(NO_ORG);
            } else{
                ResultUser resultUser = new ResultUser();
                BeanUtils.copyProperties(userVO,resultUser);
                JSONObject resultJson = new JSONObject();
                resultJson.put("user",resultUser);
                resultJson.put("code",200);
                resultJson.put("message","登录成功" );
                resultJson.put("auth",JWTAuthenticationFilter.JTW_TOKEN_HEAD + token);
                return resultJson;
            }
        }else{
            return ResultDTO.factory(ResultCodeEnum.UNAUTHORIZED);
            //throw new BadCredentialsException("密码错误");
        }


    }
    @GetMapping("refresh")
    public Object refresh(){
        UserVO userVO = ConstSystem.currUser.get();
        Claims claims =new DefaultClaims();
        Long lasttime=System.currentTimeMillis() + JWTAuthenticationFilter.JWT_EXP;
        //持有者
        claims.put("sub",userVO.getName());
        //过期时间
        claims.put("exp", new Date(lasttime));
        //权限
        claims.put("auths",null);
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(lasttime))
                .signWith(SignatureAlgorithm.HS512, JWTAuthenticationFilter.JWT_SECRET)
                .compact();
        Const.token.set(token.split("\\.")[2]);

        JSONObject resultJson = new JSONObject();
        resultJson.put("code", 200);
        resultJson.put("message", "刷新成功");
        resultJson.put("auth", JWTAuthenticationFilter.JTW_TOKEN_HEAD +  token);
        return resultJson;
    }

    @GetMapping("auth")
    @ResponseBody
    public Object auth(@RequestParam("username") String username, @RequestParam("password") String password) {

        UserDTO user = (UserDTO) securityService.loadUserByUsername(username);
        if (user == null) {
            return ResultDTO.factory(ResultCodeEnum.UNAUTHORIZED);
        }
        if (passwordEncoder.matches(password, user.getPassword())) {

            Claims claims = new DefaultClaims();
            Long lasttime = System.currentTimeMillis() + JWTAuthenticationFilter.JWT_EXP;

            //持有者
            claims.put("sub", username);
            //过期时间
            claims.put("exp", lasttime);
            //权限
            claims.put("auths", null);
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(new Date(lasttime))
                    .signWith(SignatureAlgorithm.HS512, JWTAuthenticationFilter.JWT_SECRET)
                    .compact();

            Const.token.set(token.split("\\.")[2]);
            UserVO userVO = securityService.getUser(username, Const.USER_KEY_PREFIX);
            ResultUser resultUser = new ResultUser();
            BeanUtils.copyProperties(userVO,resultUser);
            //删除之前的集团缓存，防止切换集团后无反应
            //userService.removeOrgids(user.getId());

                JSONObject resultJson = new JSONObject();
                resultJson.put("code", 200);
                resultJson.put("message", "登录成功");
                resultJson.put("auth", JWTAuthenticationFilter.JTW_TOKEN_HEAD + token);
                return resultJson;



        }else{
            return ResultDTO.factory(ResultCodeEnum.UNAUTHORIZED);
        }

    }



}
@Data
class ResultUser {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 所属部门
     */
    @TableField(value = "org_id",fill = FieldFill.INSERT)
    private Integer orgId;
    /**
     * 用户编码
     */
    private String code;
    /**
     * 用户手机
     */
    private String phone;
    /**
     * 用户名称
     */
    private String name;

    private List<Integer> orgids;

    private List<Organization> groups;


}
