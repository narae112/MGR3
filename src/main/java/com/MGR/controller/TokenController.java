package com.MGR.controller;

import com.MGR.security.JwtProvider;
import com.MGR.service.RedisService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class TokenController {

    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromRequest(request);

        if (refreshToken != null && jwtProvider.validateToken(refreshToken)) {
            Claims claims = jwtProvider.getClaims(refreshToken);
            Long userId = claims.get("id", Long.class);

            // Redis에서 리프레시 토큰을 검증
            String storedRefreshToken = redisService.getRefreshToken(userId);
            if (storedRefreshToken != null && storedRefreshToken.equals(refreshToken)) {
                String newAccessToken = jwtProvider.createToken(claims.get("email", String.class), userId);
                response.setHeader("Authorization", "Bearer " + newAccessToken);

                // 새로운 엑세스 토큰을 쿠키에 설정
                Cookie accessTokenCookie = new Cookie("at", newAccessToken);
                accessTokenCookie.setHttpOnly(true);
                accessTokenCookie.setSecure(true); // HTTPS 사용 시에만 설정
                accessTokenCookie.setPath("/");
                accessTokenCookie.setMaxAge(7 * 24 * 60 * 60);
                response.addCookie(accessTokenCookie);

                return ResponseEntity.ok().body("Token refreshed successfully");
            }
        }
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Invalid refresh token");
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
