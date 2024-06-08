package com.MGR.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final JwtProvider jwtProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                  JwtProvider jwtProvider) {
        super(authenticationManager);
        System.out.println("JwtAuthorizationFilter 시작 = " + jwtProvider);
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = getJwtFromRequest(request);
        System.out.println("doFilterInternal token = " + token);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("request = " + request.getAuthType());

        // OAuth2 인증자인지 확인
        if (authentication instanceof OAuth2AuthenticationToken) {
            chain.doFilter(request, response);
            System.out.println("OAuth2 인증자라서 리턴");
            return; // OAuth2 인증자이면, 필터를 더 이상 처리하지 않고 다음 필터로 넘어감
        }

        if (token != null && jwtProvider.validateToken(token)) {
            Authentication auth = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("JWT 토큰 검증 성공");
        } else {
            log.warn("유효하지 않은 JWT 토큰");
        }

        chain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("at".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
