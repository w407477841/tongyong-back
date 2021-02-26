package org.wyf.common.utils;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cn.hutool.http.HttpUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageSendUtils {
	
	private static final Log LOG = LogFactory.getLog(MessageSendUtils.class);

    private static String URI_SEND_SMS = "https://sms.yunpian.com/v2/sms/single_send.json";


    private final static String apikey = "9ea06bd6df426097c1f04924ab5f46cb";
    


    private final static String REGISTER_SEND_SMS = "【小驴驿站】您的注册验证码是#code#。如非本人操作，请忽略本短信";


    private final static String LOGIN_SEND_SMS = "【小驴驿站】您的登录验证码是#code#。如非本人操作，请忽略本短信";


    /**
    * 智能匹配模板接口发短信
    *
    * @param text   　短信内容
    * @param mobile 　接受的手机号
    * @return json格式字符串
    * @throws IOException
    */
    public static String send(String text, String mobile){
        Map<String, Object> params = new HashMap<>();
        params.put("apikey", apikey);
        params.put("text", text);
        params.put("mobile", mobile);
        return HttpUtil.post(URI_SEND_SMS, params);
    }


    public static String registerValidCode(String code,String mobile){
        return  send(REGISTER_SEND_SMS.replace("#code#",code),mobile);
    }

    public static String loginValidCode(String code,String mobile){
        return  send(LOGIN_SEND_SMS.replace("#code#",code),mobile);
    }


    public static void main(String[] args) {
    	MessageSendUtils.send("车辆：1231  发生 text","13962979967");
	}
}