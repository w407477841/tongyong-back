package locktest;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

public class TestLock {

    public static void main(String[] args) {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient  = Redisson.create(config);


        RLock lock = redissonClient.getLock("123456");
        boolean  res = false;
        long start = System.currentTimeMillis();
        try {

            res =   lock.tryLock(5,5,TimeUnit.SECONDS);
            System.out.println("耗时"+(System.currentTimeMillis() - start));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(res){
            System.out.println("锁住了");
            try {
                Thread.sleep(20000);
                lock.unlock();
                System.out.println("解锁了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {

            }
        }

    }

}
