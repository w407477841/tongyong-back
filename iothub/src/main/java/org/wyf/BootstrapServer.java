package org.wyf;


import org.wyf.properties.NettyProperties;
import org.wyf.properties.NettyProperties;

/**
 * 启动类接口
 *
 * @author lxr
 * @create 2017-11-18 14:05
 **/
public interface BootstrapServer {

    /**
     *  关闭
     */
    void shutdown();

    /**
     *  初始化参数
      * @param serverBean
     */
    void setServerBean(NettyProperties serverBean);
    /**
     *  启动
     */
    void start();


}
