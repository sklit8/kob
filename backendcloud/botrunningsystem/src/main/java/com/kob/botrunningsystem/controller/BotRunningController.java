package com.kob.botrunningsystem.controller;

import com.kob.botrunningsystem.service.BotRunningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bot")
public class BotRunningController {
    @Autowired
    private BotRunningService botRunningService;

    @PostMapping("/add")
    public String addBot(@RequestParam MultiValueMap<String,String> data){
        Integer userId = Integer.parseInt(data.getFirst("user_id"));
        String botCode = data.getFirst("bot_code");
        String input = data.getFirst("input");
        return botRunningService.addBot(userId,botCode,input);
    }
}
