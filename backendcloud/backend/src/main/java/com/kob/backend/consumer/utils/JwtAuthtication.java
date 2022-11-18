package com.kob.backend.consumer.utils;

import com.kob.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;

public class JwtAuthtication {
    public static Integer getUserId(String token){
        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Integer.parseInt(userId);
    }
}
