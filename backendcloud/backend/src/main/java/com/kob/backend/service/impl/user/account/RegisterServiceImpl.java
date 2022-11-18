package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String ,String > map = new HashMap<>();
        if(username == null ){
            map.put("error_message","用户名不能为空");
            return map;
        }
        if(username.length() < 4 ){
            map.put("error_message","用户名不能小于四个字符");
            return map;
        }
        if(password == null || confirmedPassword == null){
            map.put("error_message","密码不能为空");
            return map;
        }
        if(username.trim().length() == 0){
            map.put("error_message","用户名不能为空");
            return map;
        }
        if(username.length() > 20){
            map.put("error_message","用户名过长");
            return map;
        }
        if(password.length() == 0 || confirmedPassword.length() == 0){
            map.put("error_message","密码长度不能为0");
            return map;
        }
        if(password.length() > 20 || confirmedPassword.length() > 20){
            map.put("error_message","密码过长");
            return map;
        }
        if(!password.equals(confirmedPassword)){
            map.put("error_message","两次密码不一致");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        User user = userMapper.selectOne(queryWrapper);
        if(user != null){
            map.put("error_message","用户名已存在！");
            return map;
        }
        String endPassword = passwordEncoder.encode(password);
        String photo = "https://git.acwing.com/uploads/-/system/user/avatar/5566/avatar.png";
        user = new User(null,username,endPassword,photo,1500);
        userMapper.insert(user);
        map.put("error_message","注册成功");

        return map;

    }
}
