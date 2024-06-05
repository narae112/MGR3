package com.MGR.oauth2;

import com.MGR.entity.Member;
import com.MGR.repository.MemberRepository;
import com.MGR.security.JwtUtil;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.MemberService;
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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("oauth2 인증 성공= " + principal.getAttributes());

        String jwt = jwtUtil.createJwt(principal.getMember().getOauth2Id(), principal.getMember().getId());
        //jwt 토큰 생성
        System.out.println("OAuth2 jwt 토큰= " + jwt);

        response.setHeader("Authorization","Bearer" + jwt);
        //토큰값을 헤더에 추가

//        String redirectUri = UriComponentsBuilder.fromUriString("/")
//                .build().toUriString();

//        System.out.println("redirectUri = " + redirectUri);

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