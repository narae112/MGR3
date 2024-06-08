package com.MGR.security;

//import com.MGR.service.OAuth2MemberService;
import com.MGR.entity.Member;
import com.MGR.service.MemberService;
import com.MGR.service.OAuth2MemberService;
import com.MGR.service.PrincipalDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.expiredTime}")
    private Long expiredTime;

    private final PrincipalDetailsService principalDetailsService;
    private final OAuth2MemberService oAuth2MemberService;
    private final MemberService memberService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public String getMemberId(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    public Long getId(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("id", Long.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT 토큰 검증 실패: {}", e.getMessage());
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
        System.out.println("getAuthentication id = " + id);
        System.out.println(claims.getSubject());

        Member member = memberService.findById(id).orElseThrow();
        String memberEmail = member.getEmail();

        String email = claims.get("email", String.class);
        System.out.println("getAuthentication email = " + email);

       UserDetails userDetails = principalDetailsService.loadUserByUsername(memberEmail);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String createToken(String email, Long id, boolean isOauth2) {
        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("id", id);
        claims.setSubject(isOauth2 ? "oauth2_" + email : email); // 주체 설정
        System.out.println("createToken claims 발행 확인 = " + claims);
        System.out.println(claims.getSubject());
        System.out.println("createToken email = " + email);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        System.out.println("token 발행 확인 = " + token);
        return token;
    }
}
