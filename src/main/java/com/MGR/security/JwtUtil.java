package com.MGR.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    @Value("${jwt.secretKey}") //application.properties에 저장되어 있는 값을 가져온다.
    private String secretKey;
    @Value("${jwt.expiredMs}") //application.properties에 저장되어 있는 값을 가져온다.
    private Long expiredMs;

    public String getMemberId(String token) {
//        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
//                .getBody().get("memberId", String.class);
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("memberId", String.class);
    }
    public Long getId(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("id", Long.class);
    }

    public boolean isExpired(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().getExpiration().before(new Date());
    }

    public String createJwt(String memberId, Long id) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);
        claims.put("id", id);
        System.out.println("jwt 발행 확인 = " + claims);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        System.out.println("token 발행 확인 = " + token);
        return token;
    }
}
