package org.wyf.system;

import org.wyf.system.model.Organization;
import org.wyf.system.model.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.wyf.system.model.Organization;
import org.wyf.system.model.User;

import java.util.List;

/**
* @author: wangyifei
* Description:
* Date: 11:22 2018/9/4
*/
@Data
public class UserVO extends User {
	private static final long serialVersionUID = 1L;
	
	private List<Integer>  orgids;

	private List<Organization> groups;

	public static UserVO factory(User user){
		
		UserVO  userVO = new UserVO();
		
		BeanUtils.copyProperties(user, userVO);
		
		return userVO;
	}
	
	
}
