package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.service.pk.StartGamesService;
import org.springframework.stereotype.Service;

@Service
public class StartGamesServiceImpl implements StartGamesService {

    @Override
    public String startGame(Integer aId, Integer aBotId,Integer bId,Integer bBotId) {
        WebSocketServer.startGame(aId,aBotId,bId,bBotId);
        return "start game success";
    }
}
