package com.kob.botrunningsystem.service.impl.utils;


import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class Consumer extends Thread{

    private static RestTemplate restTemplate;
    private Bot bot;
    private static final String receiveBotMoveUrl = "http://nacos-backend/pk/receive/bot/move";

    @Autowired
    private void setRestTemplate(RestTemplate restTemplate){
        Consumer.restTemplate = restTemplate;
    }
    public void startTimeOut(long timeout,Bot bot){
        this.bot = bot;
        this.start();

        try {
            this.join(timeout);//最多等到timeout秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.interrupt();//终断当前线程
        }
    }

    private String addUid(String code,String uid){
        int k = code.indexOf(" implements java.util.function.Supplier<Integer>");
        return code.substring(0,k)+uid+code.substring(k);
    }

    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0,8);
        Supplier<Integer> botInterface = Reflect.compile(
                "com.kob.botrunningsystem.utils.Bot"+uid,
                addUid(bot.getBotCode(),uid)
        ).create().get();

        //读入代码写入文件
        //TODO docker沙箱
        File file = new File("input.txt");
        try(PrintWriter fout = new PrintWriter(file)){
            fout.println(bot.getInput());
            fout.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Integer direction = botInterface.get();

        //向主服务器发送运行结果
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",bot.getUserId().toString());
        data.add("direction",direction.toString());
        System.out.println(bot.getUserId()+"  "+direction.toString());
        //发送到主服务器
        restTemplate.postForObject(receiveBotMoveUrl,data, String.class);
    }
}
