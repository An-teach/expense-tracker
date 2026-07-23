package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // 最新版推薦回傳 javax.crypto.SecretKey
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // 生成 JWT Token (最新語法：subject(), issuedAt(), expiration(), signWith())
    public String generateJwtToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key()) // 0.12.x 會自動根據 SecretKey 判斷加密演算法 (HS256)
                .compact();
    }

    // 從 JWT Token 中獲取 Username (最新語法：Jwts.parser().verifyWith().build().parseSignedClaims())
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 驗證 JWT Token 是否合法 (最新語法：parseSignedClaims)
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }
}