package com.kob.botrunningsystem.service.impl.utils;


import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bot代码执行线程
 */
public class BotPool extends Thread{
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private Queue<Bot> bots = new LinkedList<>();


    public void addBot(Integer userId,String botCode,String input){
        lock.lock();
        try{
            bots.add(new Bot(userId,botCode,input));
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }

    private void consume(Bot bot){
        Consumer consumer = new Consumer();
        consumer.startTimeOut(2000,bot);
    }

    @Override
    public void run() {
        while (true){
            lock.lock();
            if(bots.isEmpty()){
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    lock.unlock();
                    e.printStackTrace();
                    break;
                }
            }else {
                Bot bot = bots.remove();
                lock.unlock();
                consume(bot);//比较耗时，可能会执行几秒钟
            }
        }
    }
}
