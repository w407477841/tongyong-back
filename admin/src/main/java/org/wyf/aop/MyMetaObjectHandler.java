package org.wyf.aop;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.wyf.common.constant.Const;
import org.wyf.system.ConstSystem;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.wyf.system.ConstSystem;

import java.lang.reflect.Field;
import java.util.Date;

/**
* 自动填充属性
* @author: wangyifei
* Description:
* Date: 15:29 2018/8/30
*/
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
	
	@Override
	public void insertFill(MetaObject metaObject) {

		Field field = null;
		try {
			 field = metaObject.getOriginalObject().getClass().getDeclaredField("orgId");

		}catch (Exception e) {

		}
		//存在ordid就填充
		if(field != null){
			Object orgId = getFieldValByName("orgId", metaObject);
			if(null== orgId){
				//setFieldValByName("orgId", Const.orgId.get(), metaObject);
				this.setInsertFieldValByName("orgId", Const.orgId.get(), metaObject);
			}
		}


		/**必有的字段*/
		Object isDel =  metaObject.getValue("isDel");
		Object createTime = metaObject.getValue("createTime");
		Object createtUser  =metaObject.getValue("createUser");
		// 没有手动塞值就填充
		if(null == isDel ){
			setInsertFieldValByName("isDel", 0,metaObject);
		}
		if(null==createTime){
			setInsertFieldValByName("createTime", new Date(),metaObject);
		}
		if(null == createtUser){
			if(ConstSystem.currUser.get()!=null){
				setInsertFieldValByName("createUser",  ConstSystem.currUser.get().getId(),metaObject);
			}

		}
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		Object modifyTime = getFieldValByName("modifyTime", metaObject);
        Object modifyUser = getFieldValByName("modifyUser", metaObject);


			// 添加修改时间
		setUpdateFieldValByName("modifyTime", new Date(System.currentTimeMillis()), metaObject);
			// 添加修改人
			if(ConstSystem.currUser.get()!=null){
				setUpdateFieldValByName("modifyUser", ConstSystem.currUser.get().getId(), metaObject);
			}


		
	}

}
