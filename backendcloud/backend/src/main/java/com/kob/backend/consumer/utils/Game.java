package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    private final Integer rows;//行
    private final Integer cols;//列
    private final Integer inner_walls_count;//墙的数量
    private final int[][] g;//地图
    private final static int[] dx = {-1,0,1,0},dy = {0,1,0,-1};
    private final Player playerA,playerB;//玩家A,B
    private Integer nextStepA = null;//A玩家的下一步
    private Integer nextStepB = null;//B玩家的下一步
    private ReentrantLock lock = new ReentrantLock();
    private String Status = "playing";//游戏状态
    private String loser = "";//谁是loser
    private static String addBotUrl = "http://127.0.0.1:3002/bot/add";
    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Bot botA, Integer idB,Bot botB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        Integer botIdA = -1,botIdB = -1;
        String botCodeA = "",botCodeB = "";
        if(botA != null){
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if(botB != null){
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        playerA = new Player(idA,botIdA,botCodeA,this.rows-2,1,new ArrayList<>());
        playerB = new Player(idB,botIdB,botCodeB,1,this.cols-2,new ArrayList<>());
    }

    public Player getPlayerA(){
        return playerA;
    }
    public Player getPlayerB(){
        return playerB;
    }

    public int[][] getG(){
        return g;
    }

    public void setNextStepA(Integer nextStepA){
        lock.lock();
        try{
            this.nextStepA = nextStepA;
        }finally {
            lock.unlock();
        }
    }
    public void setNextStepB(Integer nextStepB){
        lock.lock();
        try{
            this.nextStepB = nextStepB;
        }finally {
            lock.unlock();
        }
    }

    /**
        判断地图是否连通
     */
    private boolean check_connectivity(int sx,int sy,int tx,int ty){
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x<this.rows && y >= 0 && y<this.cols && g[x][y] == 0){
                if(check_connectivity(x,y,tx,ty)){
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 绘制地图
     * @return
     */
    public boolean draw(){
        //初始化地图
        for(int i = 0 ; i<this.rows;i++){
            for (int j = 0;j<this.cols;j++){
                g[i][j] = 0;
            }
        }

        //给四周加上墙壁
        for(int r = 0;r<this.rows;r++){
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for(int c = 0;c<this.cols;c++){
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        //随机生成障碍物
        Random random = new Random();
        for(int i = 0;i<this.inner_walls_count / 2;i++){
            for(int j = 0;j<1000;j++){
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if(g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1)  continue;;
                if(r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)    continue;

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }
        return check_connectivity(this.rows - 2,1,1,this.cols - 2);
    }

    public void createMap(){
        for(int i = 0;i<1000;i++){
            if(draw())
                break;
        }
    }


    /**
     * 将当前的局面信息编码成字符串
     * @param player
     * @return
     */
    public String getInput(Player player){
        Player me,you;
        //判断me you玩家
        if(playerA.getId().equals(player.getId())){
            me = playerA;
            you = playerB;
        }else{
            me = playerB;
            you = playerA;
        }
        return getMapString()+"#"
                +me.getSx()+"#"
                +me.getSy()+"#("
                +me.getStepsString()+")#"
                +you.getSx()+"#"
                +you.getSy()+"#("
                +you.getStepsString()+")#";
    }

    /**
     *发送Bot代码
     */
    public void sendBotCode(Player player){
        if(player.getBotId().equals(-1)) return ;//人工操作直接返回
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("user_id",player.getId().toString());
        data.add("bot_code",player.getBotCode());
        data.add("input",getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl,data,String.class);
    }

    /**
     * 判断玩家是否有操作
     * @return
     */
    public boolean nextStep(){//等待两名玩家的操作
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        sendBotCode(playerA);
        sendBotCode(playerB);

        for(int i = 0 ;i<150;i++){
            try{
                Thread.sleep(50);
                lock.lock();
                try{
                    if(nextStepA != null && nextStepB != null){
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                }finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 判断游戏是否结束
     * @param cellsA
     * @param cellsB
     * @return
     */
    public boolean check_valid(List<Cell> cellsA,List<Cell> cellsB){
        int n = cellsA.size();
        Cell cell = cellsA.get(n-1);
        if(g[cell.x][cell.y] == 1) return false;

        for(int i = 0;i<n - 1;i++){
            if(cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y) return false;
        }

        for(int i = 0;i<n-1;i++){
            if(cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y) return false;
        }

        return true;
    }

    /**
     * 判断赢家
     */
    public void judge(){
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();
        boolean validA = check_valid(cellsA,cellsB);
        boolean validB = check_valid(cellsB,cellsA);
        if(! validA || !validB){
            Status = "finished";
            if(!validA && !validB){
                loser = "all";
            }else if(!validA){
                loser = "A";
            }else {
                loser = "B";
            }
        }
    }

    public void sendMove(){//向两名玩家发送移动信息
        lock.lock();
        try{
            JSONObject resp = new JSONObject();
            resp.put("event","move");
            resp.put("a_direction",nextStepA);
            resp.put("b_direction",nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        }finally {
            lock.unlock();
        }

    }

    public void sendAllMessage(String message){
        if(WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if(this.playerB.getId() != 8 && WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private String getMapString(){
        StringBuilder res = new StringBuilder();
        for(int i = 0 ;i <this.rows;i++){
            for(int j = 0;j<this.cols;j++){
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    /**
     * 更新天梯积分
     */
    public void updateUserRating(Player player,Integer rating){
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    //发送到数据库
    public void sendToDatabase(){
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();

        if("A".equals(loser)){
            ratingA -= 3;
            ratingB += 5;
        }else{
            ratingA += 5;
            ratingB -= 3;
        }

        updateUserRating(playerA,ratingA);
        updateUserRating(playerB,ratingB);

        Record record = new Record(null,playerA.getId(),playerA.getSx(),playerA.getSy(),
                playerB.getId(),playerB.getSx(),playerB.getSy(),playerA.getStepsString(),playerB.getStepsString()
                ,getMapString(),loser,new Date());
        WebSocketServer.recordMapper.insert(record);
    }

    //返回游戏结果
    public void sendResult(){//向两个用户发送结果
        JSONObject resp = new JSONObject();
        resp.put("event","result");
        resp.put("loser",loser);
        sendToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {
        for(int i = 0 ;i < 1000; i ++){
            if(nextStep()){//是否获取到两条蛇的操作
                judge();
                if(Status.equals("playing")){
                    sendMove();
                }else{
                    sendResult();
                    break;
                }
            }else{
                Status = "finished";
                lock.lock();
                try{
                    if(nextStepA == null && nextStepB == null){
                        loser = "all";
                    }else if(nextStepA == null){
                        loser = "A";
                    }else {
                        loser = "B";
                    }
                }finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
