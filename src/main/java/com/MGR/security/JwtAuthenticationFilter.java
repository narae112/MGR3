package com.MGR.security;

import com.MGR.entity.Member;
import com.MGR.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final RedisService redisService;

    //로그인을 시도할 때 실행
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("attemptAuthentication 시작");
        Member member = new Member();

        try {
            // URL 인코딩된 폼 데이터 읽기
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            System.out.println("URL 인코딩된 폼 데이터 email = " + email);

            member.setEmail(email);
            member.setPassword(password);

        } catch (BadCredentialsException e) {
            System.out.println("로그인 정보 에러 = " + e.getMessage());
        } catch (Exception e) {
            System.out.println("json 읽어오는 중 에러 = " + e.getMessage());
        }


        //토큰 생성
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());
        System.out.println("JWT token 생성 = " + token);

        try {
            // authenticationManager 의 authenticate 메소드 실행
            Authentication authenticate = authenticationManager.authenticate(token);
            log.info("인증 성공");
            return authenticate;
        } catch (AuthenticationException e) {
            log.error("인증 실패: {}", e.getMessage(), e);
            try {
                // 실패 처리
                customAuthenticationFailureHandler.onAuthenticationFailure(request, response, e);
            } catch (IOException | ServletException ex) {
                log.error("Authentication failure handler 처리 중 에러: {}", ex.getMessage(), ex);
            }
            return null;
        }

    }

    //인증을 성공하면 실행
    //response Authorization header 에 jwt 를 담아서 보내줌
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principal = (PrincipalDetails) authResult.getPrincipal();
        Member member = principal.getMember();
        String jwt = jwtProvider.createToken(member.getEmail(), member.getId());
        response.setHeader("Authorization", "Bearer " + jwt);

        // JWT를 쿠키에 설정 (Access Token)
        Cookie accessTokenCookie = new Cookie("at", jwt);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTPS 사용 시에만 설정
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(5 * 60);
        response.addCookie(accessTokenCookie);

        // Refresh Token 생성 및 쿠키에 설정
        String refreshToken = jwtProvider.createRefreshToken(principal.getMember().getOauth2Id(), principal.getMember().getId());
        Cookie refreshTokenCookie = new Cookie("rt", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS 사용 시에만 설정
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(12 * 60 * 60); // 12시간 동안 유효
        response.addCookie(refreshTokenCookie);

        // Redis 에 RefreshToken 저장
        redisService.saveRefreshToken(principal.getMember().getId(), refreshToken);

        response.sendRedirect("/");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        getFailureHandler().onAuthenticationFailure(request, response, failed);
    }
}