package com.MGR.oauth2;

import com.MGR.security.JwtUtil;
import com.MGR.security.PrincipalDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println(principal.getAttributes());
        System.out.println("oauth2 인증 성공");

        String jwt = jwtUtil.createJwt(principal.getMember().getOauth2Id(), principal.getMember().getId());
        System.out.println("OAuth2 jwt 토큰= " + jwt);

        response.setHeader("Authorization","Bearer" + jwt);
        //토큰값을 헤더에 추가

//        String redirectUri = UriComponentsBuilder.fromUriString("/")
//                .queryParam("accessToken", jwt).build().toUriString();
        // 토큰값을 url 로 보내는 방식

        String redirectUri = UriComponentsBuilder.fromUriString("/")
                .build().toUriString();

        System.out.println("redirectUri = " + redirectUri);

        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}