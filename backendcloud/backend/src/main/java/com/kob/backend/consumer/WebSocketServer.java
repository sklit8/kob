package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthtication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private User user;
    private Session session = null;
    public static UserMapper userMapper;
    public static RecordMapper recordMapper;
    public static BotMapper botMapper;
    public static RestTemplate restTemplate;
    public Game game = null;
    private final static String addPlayerUrl = "http://nacos-matching/player/add";
    private final static String removePlayerUrl = "http://nacos-matching/player/remove";

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        WebSocketServer.botMapper = botMapper;
    }
    @Autowired
    public void setRecordMapper(RecordMapper recordMapper){
        WebSocketServer.recordMapper = recordMapper;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate = restTemplate;
    }


    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session;
        Integer userId = JwtAuthtication.getUserId(token);
        this.user = userMapper.selectById(userId);

        if (this.user != null) {
            users.put(userId, this);
        } else {
            this.session.close();
        }

    }

    @OnClose
    public void onClose() {
        if (this.user != null) {
            users.remove(this.user.getId());
        }
    }

    /**
     * 创建一场游戏对局
     * @param aId   a玩家Id
     * @param bId   b玩家Id
     */
    public static void startGame(Integer aId,Integer aBotId,Integer bId,Integer bBotId){
        if(bId == 8){
            users.put(8,new WebSocketServer());
        }
        User a = userMapper.selectById(aId),b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId),botB = botMapper.selectById(bBotId);

        Game game = new Game(13, 14, 20,a.getId(),botA,b.getId(),botB);
        game.createMap();
        if(users.get(a.getId()) != null)
            users.get(a.getId()).game = game;
        if(users.get(b.getId()) != null)
            users.get(b.getId()).game = game;

        //开启线程
        game.start();

        //封装JSON数据
        JSONObject respGame = new JSONObject();
        respGame.put("a_id",game.getPlayerA().getId());
        respGame.put("a_sx",game.getPlayerA().getSx());
        respGame.put("a_sy",game.getPlayerA().getSy());
        respGame.put("b_id",game.getPlayerB().getId());
        respGame.put("b_sx",game.getPlayerB().getSx());
        respGame.put("b_sy",game.getPlayerB().getSy());
        respGame.put("map",game.getG());

        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame);
        if(users.get(a.getId()) != null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);
        if(bId !=8 && users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
    }

    /**
     * 开始匹配
     */
    private void startMatching(Integer botId) {
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        data.add("rating",this.user.getRating().toString());
        data.add("bot_id",botId.toString());
        //发送到匹配系统服务器
        restTemplate.postForObject(addPlayerUrl,data,String.class);

    }
    /**
     * 取消匹配
     */
    private void stopMatching() {
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",this.user.getId().toString());
        //发送到匹配系统服务器
        restTemplate.postForObject(removePlayerUrl,data,String.class);
    }

    /**
     * 获取玩家移动方向
     * @param direction
     */
    public void move(int direction){
        if(game.getPlayerA().getId().equals(user.getId())){
            if(game.getPlayerA().getBotId().equals(-1))
                game.setNextStepA(direction);
        }else if(game.getPlayerB().getId().equals(user.getId())){
            if(game.getPlayerA().getBotId().equals(-1))
                game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {  // 当做路由
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching-ai".equals(event)){
            startGame(this.user.getId(), data.getInteger("bot_id"), 8, 1);
        }else if ("start-matching".equals(event)) {
                startMatching(data.getInteger("bot_id"));
        } else if ("stop-matching".equals(event)) {
                stopMatching();
        } else if("move".equals(event)){
                move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
