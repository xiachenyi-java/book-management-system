package com.example2.demo2.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 夏辰义
 * 2026/8/2114:52
 */
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;



    // 把字符串密钥转换成 jjwt 需要的 SecretKey 对象
    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    //生成令牌
    public String generateToken(Integer userId,String username, String role){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))          // 【Payload】主题，放用户ID
                .claim("username", username)           // 【Payload】自定义数据，放用户名
                .claim("role",role)
                .issuedAt(now)                            // 【Payload】签发时间
                .expiration(expiryDate)                   // 【Payload】过期时间
                .signWith(getKey(), Jwts.SIG.HS256)       // 【Signature】签名：密钥 + 算法
                .compact();                               // 压缩成字符串
    }

    //解析令牌
    public Claims parseToken(String token){
        return Jwts.parser()
                .verifyWith(getKey())            //用同样的密钥验签
                .build()
                .parseSignedClaims(token)       // 解析（如果签名不对或过期，这里会抛异常）
                .getPayload();                  // 拿到 Payload 部分
    }

    //提取token信息
    public Integer getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Integer.valueOf(claims.getSubject());
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }
}
