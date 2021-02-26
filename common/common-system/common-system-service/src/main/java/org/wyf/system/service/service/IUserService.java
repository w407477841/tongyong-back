package org.wyf.system.service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.vo.UserVo;
import org.wyf.system.model.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.wyf.common.dto.RequestDTO;
import org.wyf.system.model.User;
import org.wyf.system.vo.UserVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyf123
 * @since 2018-08-13
 */
public interface IUserService extends IService<User> {
    /**
     * 新增
     * @param userVo
     * @return
     * @throws Exception
     */
    boolean insertUser(UserVo userVo) throws Exception;

    /**
     * 修改
     * @param userVo
     * @return
     * @throws Exception
     */
    boolean updateUser(UserVo userVo) throws Exception;

    /**
     * 获取用户信息列表
     * @param page
     * @param requestDTO
     * @return
     * @throws Exception
     */
    List<UserVo> selectUserInfo(Page<User> page, RequestDTO<User> requestDTO) throws Exception;

    /**
     * 获取部门下用户
     * @param page
     * @param requestDTO
     * @return
     * @throws Exception
     */
    List<User> selectUserByOrgId(Page<User> page, RequestDTO<User> requestDTO) throws Exception;

    /**
     * 获取未加入该部门下的用户
     * @param page
     * @param requestDTO
     * @return
     * @throws Exception
     */
    List<User> selectUserNotInOrg(Page<User> page, RequestDTO<User> requestDTO) throws Exception;

    /**
     * 根据id获取用户
     * @param requestDTO
     * @return
     * @throws Exception
     */
    UserVo selectUserById(RequestDTO<User> requestDTO) throws Exception;

    /**
     * 修改密码
     * @param requestDTO
     * @return
     * @throws Exception
     */
    boolean changePWD(RequestDTO<UserVo> requestDTO) throws Exception;

    List<User> getListUserByIds(@Param("ids") String[] ids);


}
