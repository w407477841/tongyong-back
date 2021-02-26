package org.wyf.properties;

import org.wyf.common.CoderChain;
import org.wyf.common.SimpleCoderChain;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : wangyifei
 * Description
 * Date: Created in 9:26 2018/12/11
 * Modified By : wangyifei
 */
@ConfigurationProperties(prefix = "netty")
@Data
public class NettyProperties {

   /**复用*/
   private  boolean  reuseaddr;
   /**堆积*/
   private int backlog;
   /**接收区缓存*/
   private int revbuf;
   /**发送区缓存*/
   private int sndbuf;
   /** 立即发送*/
   private boolean tcpNodelay;
   /** 保持 */
   private boolean keepalive;
   /** 端口 */
   private int  port;
   /** 心跳*/
   private int heart;
   /**  处理类 */
   private  Class<SimpleChannelInboundHandler> handler;
   /**
    * 编/解码器 容器
    */
   private Class<? extends CoderChain> coderChain = SimpleCoderChain.class;

   /**  */
   private int bossThread;
   /**  */
   private int workThread;


}
