package com.kob.botrunningsystem;

import com.kob.botrunningsystem.service.impl.BotRunningServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class BotRunningSystemApplication {
    public static void main(String[] args) {
        BotRunningServiceImpl.botPool.start();//启动代码执行线程
        SpringApplication.run(BotRunningSystemApplication.class,args);
    }
}
