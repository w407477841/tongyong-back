package org.wyf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author wangyifei
 */
@SpringBootApplication
@EnableScheduling
public class TimerApplictaion {

    public static void main(String[] args) {

        SpringApplication.run(TimerApplictaion.class,args);

    }

}
