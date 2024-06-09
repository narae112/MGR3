package com.MGR.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        try {
            String token = getJwtFromRequest(request);
            System.out.println("doFilterInternal token = " + token);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("request = " + request.getAuthType());

            if (token != null && jwtProvider.validateToken(token)) {
                Authentication auth = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("JWT 토큰 검증 성공");
            } else {
                System.out.println("유효하지 않은 JWT 토큰");
            }
        } catch (BadCredentialsException e){
            // 인증 실패 시 login/error URL로 리다이렉트
            response.sendRedirect("/login/error");
            return;
        } catch (Exception e) {
            // 예외 발생 시 SecurityContext를 클리어하고 예외를 던져서 기본 예외 처리기가 작동하도록 함
            SecurityContextHolder.clearContext();
            chain.doFilter(request, response);
            return;
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
