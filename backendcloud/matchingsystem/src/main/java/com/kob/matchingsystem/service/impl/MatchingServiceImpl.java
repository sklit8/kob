package com.kob.matchingsystem.service.impl;

import com.kob.matchingsystem.service.MatchingService;
import com.kob.matchingsystem.service.impl.utils.MatchingPool;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    public final static MatchingPool matchingPool = new MatchingPool();

    /**
     * 添加匹配玩家
     * @param userId    玩家Id
     * @param rating    玩家Rating
     * @return
     */
    @Override
    public String addPlayer(Integer userId, Integer rating,Integer botId) {
        matchingPool.addPlayer(userId,rating,botId);
        return "add success";
    }

    /**
     *移除玩家
     * @param userId    玩家Id
     * @return
     */
    @Override
    public String removePlayer(Integer userId) {
        matchingPool.removePlayer(userId);
        return "remove success";
    }
}
