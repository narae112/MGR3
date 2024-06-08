package com.MGR.oauth2;

import com.MGR.entity.Member;
import com.MGR.security.JwtProvider;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("oauth2 인증 성공= " + principal.getAttributes());

        //jwt 토큰 생성
        String jwt = jwtProvider.createToken(principal.getMember().getOauth2Id(), principal.getMember().getId(), true);
        System.out.println("OAuth2 jwt 토큰= " + jwt);

        // JWT를 쿠키에 설정
        Cookie cookie = new Cookie("at", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS 사용 시에만 설정
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1일 동안 유효
        response.addCookie(cookie);

        // 인증 객체 생성 및 SecurityContext에 설정
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        String redirectUri = null;

        Optional<Member> findMember = memberService.findByEmail(principal.getUsername());//Username -> email
        if(findMember.isPresent()){
            redirectUri = "/member/changePasswordForm";
            //이미 가입된 email 이 없으면 비밀번호 변경 화면으로 이동
        }else {
            redirectUri = "/";
            // 기존 사용자는 로그인 후 메인으로
        }
        System.out.println("redirectUri = " + redirectUri);

        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}