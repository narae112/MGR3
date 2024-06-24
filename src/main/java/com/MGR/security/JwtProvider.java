package com.MGR.security;

import com.MGR.entity.Member;
import com.MGR.service.MemberService;
import com.MGR.service.PrincipalDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.accessTokenExpiredTime}")
    private Long accessTokenExpiredTime;
    @Value("${jwt.refreshTokenExpiredTime}")
    private Long refreshTokenExpiredTime;

    private final PrincipalDetailsService principalDetailsService;
    private final MemberService memberService;

    public Long getId(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("id", Long.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {

            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Long id = claims.get("id", Long.class);

        String email = memberService.findById(id).get().getEmail();

       UserDetails userDetails = principalDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String createRefreshToken(String email, Long id) {
        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("id", id);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiredTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        return token;
    }

    public String createToken(String email, Long id) {
        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("id", id);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiredTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return token;
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("email", String.class);
    }
}
