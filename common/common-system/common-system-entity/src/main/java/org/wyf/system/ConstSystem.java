package org.wyf.system;


public class ConstSystem {

    /**
     * 当前用户
     */
    public static ThreadLocal<UserVO> currUser =  new ThreadLocal<>();

}
