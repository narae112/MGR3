package com.MGR.security;

import com.MGR.dto.MemberFormDto;
import com.MGR.entity.Member;
import com.MGR.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    //로그인을 시도할 때 실행
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("attemptAuthentication 시작");
        Member member = new Member();

        try {
            // URL 인코딩된 폼 데이터 읽기
            String email = request.getParameter("email");
            System.out.println("email = " + email);
            String password = request.getParameter("password");
            System.out.println("password = " + password);

            member.setEmail(email);
            member.setPassword(password);

            System.out.println("member 로그인 정보 = " + member);

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
        String jwt = jwtProvider.createToken(member.getEmail(), member.getId(), false);
        response.setHeader("Authorization", "Bearer " + jwt);

        // JWT를 쿠키에 설정
        Cookie cookie = new Cookie("at", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS 사용 시에만 설정
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1일 동안 유효
        response.addCookie(cookie);
        System.out.println("JWT 토큰 생성 및 쿠키에 추가 완료");

        response.sendRedirect("/");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        getFailureHandler().onAuthenticationFailure(request, response, failed);
    }
}