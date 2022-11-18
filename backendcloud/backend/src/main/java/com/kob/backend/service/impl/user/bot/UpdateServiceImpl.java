package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> update(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int bot_id = Integer.parseInt(data.get("bot_id"));
        Map<String ,String > map = new HashMap<>();
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        if(title == null || title.length() == 0){
            map.put("error_message"," 标题不能为空");
            return map;
        }

        if(title.length() > 100){
            map.put("error_message","标题不能超过100");
            return map;
        }

        if(description == null ||description.length() == 0){
            description = "这个用户很懒，什么也没有留下~";
        }

        if(description.length() > 300){
            map.put("error_message","描述长度不能超过300");
            return map;
        }

        if(content == null ||content.length() == 0){
            map.put("error_message","代码不能为空");
            return map;
        }

        if(content.length() > 10240){
            map.put("error_message","代码长度不能超过10240");
            return map;
        }
        Bot bot = botMapper.selectById(bot_id);
        if(bot == null){
            map.put("error_message","你的bot不存在或已被删除");
            return map;
        }
        if(bot.getUserId() != user.getId()){
            map.put("error_meeage","没有权限进行此操作");
            return map;
        }
        Date now = new Date();
        Bot new_bot = new Bot(bot.getId(), user.getId(), title,description,content,bot.getCreatetime(),now);
        botMapper.updateById(new_bot);
        map.put("error_message","success");
        return map;
    }
}
