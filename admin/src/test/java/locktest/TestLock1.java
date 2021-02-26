package locktest;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

public class TestLock1 {

    public static void main(String[] args) {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient  = Redisson.create(config);


        RLock lock = redissonClient.getLock("123456");
        boolean  res = false;
        try {
            res =   lock.tryLock(10L,30L,TimeUnit.SECONDS);
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
//                lock.unlock();
//                System.out.println("解锁了");
            }
        }

    }

}
