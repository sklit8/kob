package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread{
    private static List<Player> players = new ArrayList<>(); //玩家匹配队列
    private ReentrantLock lock = new ReentrantLock();//锁
    private static RestTemplate restTemplate;
    private final static String startGameUrl = "http://nacos-backend/pk/start/game";
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        MatchingPool.restTemplate = restTemplate;
    }
    /**
     * 添加一名玩家到匹配池
     */
    public void addPlayer(Integer userId,Integer rating,Integer botId){
        lock.lock();
        try{
            players.add(new Player(userId,botId,rating,0));
        }finally {
            lock.unlock();
        }
    }

    /**
     * 从匹配池移除玩家
     */
    public void removePlayer(Integer userId){
        lock.lock();
        try{
            List<Player> newPlayers = new ArrayList<>();
            for(Player p : players){
                if(!p.getUserId().equals(userId)){
                    newPlayers.add(p);
                }
            }
            players = newPlayers;
        }finally {
            lock.unlock();
        }
    }


    private void increasingWaitingTime(){ //匹配等待时间加一
        for(Player p : players){
            p.setWaitingTime(p.getWaitingTime()+1);
        }
    }

    /**
     * 判断两名玩家是否匹配
     * @param a
     * @param b
     * @return
     */
    private boolean check_matched(Player a, Player b){
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        int waitingTime = Math.min(a.getWaitingTime(),b.getWaitingTime());
        return ratingDelta <= waitingTime * 10;
    }

    /**
     * 返回a，b的匹配结果
     * @param a
     * @param b
     */
    private void sendResult(Player a,Player b){
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("a_id",a.getUserId().toString());
        data.add("a_bot_id",a.getBotId().toString());
        data.add("b_id",b.getUserId().toString());
        data.add("b_bot_id",b.getBotId().toString());
        //发送到主服务器
        restTemplate.postForObject(startGameUrl,data,String.class);
    }

    /**
     * 匹配玩家
     */
    private void matchPlayer(){
        int len = players.size();
        boolean[] used = new boolean[len];
        for(int i = 0 ; i < len ; i++){
            if(used[i]) continue;
            for(int j = i +1 ;j < len ;j ++){
                if(used[j]) continue;
                Player a = players.get(i),b = players.get(j);
                System.out.println("matched "+a.getUserId()+" and "+b.getUserId());
                if(check_matched(a,b)){
                    used[i] = used[j] = true;
                    sendResult(a,b);
                    break;
                }
            }
        }
        //将未匹配的继续添加到队列
        List<Player> newPlayers = new ArrayList<>();
        for(int i = 0;i<len;i++){
            if(!used[i]){
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000);
                lock.lock();;
                try{
                    increasingWaitingTime();
                    matchPlayer();
                }finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
