package org.wyf.auto;


import org.wyf.NettyBootstrapServer;
import org.wyf.BootstrapServer;
import org.wyf.properties.NettyProperties;
import org.wyf.properties.NettyProperties;

/**
 * 初始化服务
 *
 * @author lxr
 * @create 2017-11-29 20:12
 **/
public class InitServer {

    private NettyProperties serverBean;

    public InitServer(NettyProperties serverBean) {
        this.serverBean = serverBean;
    }

    BootstrapServer bootstrapServer;

    public void open(){
        if(serverBean!=null){
            bootstrapServer = new NettyBootstrapServer();
            bootstrapServer.setServerBean(serverBean);
            bootstrapServer.start();
        }
    }


    public void close(){
        if(bootstrapServer!=null){
            bootstrapServer.shutdown();
        }
    }

}
