//package com.MGR.config;
//
//import com.MGR.security.JwtProvider;
//import com.MGR.service.PrincipalDetailsService;
//import io.jsonwebtoken.Claims;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//
//import java.util.Arrays;
//import java.util.Map;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class WebSocketCookieInterceptor implements HandshakeInterceptor {
//
//    private final JwtProvider jwtProvider;
//
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//        System.out.println("Before Handshake");
//
//        String cookie = request.getHeaders().getFirst("cookie");
//        if (cookie != null) {
//            String accessToken = extractTokenFromCookie(cookie, "at");
//            if (accessToken != null && jwtProvider.validateToken(accessToken)) {
//                Claims claims = jwtProvider.getClaims(accessToken);
//                Long userId = claims.get("id", Long.class);
//                String userEmail = claims.get("email", String.class);
//                attributes.put("userId", userId);
//                attributes.put("userEmail", userEmail);
//                return true;
//            } else {
//                System.out.println("Invalid JWT token");
//                return false;
//            }
//        } else {
//            System.out.println("No JWT token found in cookies");
//            return false;
//        }
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
//        System.out.println("After Handshake");
//    }
//
//    private String extractTokenFromCookie(String cookie, String tokenName) {
//        System.out.println("extractTokenFromCookie 여기서 쿠키 = " + cookie);
//        String[] cookies = cookie.split(";");
//        for (String c : cookies) {
//            String[] cookiePair = c.split("=");
//            if (cookiePair[0].trim().equals(tokenName)) {
//                return cookiePair[1].trim();
//            }
//            System.out.println("쿠키리스트의 쿠키 = " + c);
//            System.out.println("cookiePair = " + Arrays.toString(cookiePair));
//        }
//        return null;
//    }
//}
