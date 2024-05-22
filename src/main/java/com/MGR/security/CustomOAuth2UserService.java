package com.MGR.security;

import com.MGR.entity.Member;
import com.MGR.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final CustomUserDetailsService customUserDetailsService;
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        OAuth2User user = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> userInfo = user.getAttributes();

        String email = "";
        String name = "";

//        switch (provider){
//            case "kakao":
//                Map<String,Object> kakaoAccount = user.getAttribute("kakao_account");
//                email = (String) kakaoAccount.get("email");
//                name = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname");
//                break;
//        }

        log.info("provider: {} -> email: {}, name{}", provider, email, name);

        Member member = memberRepository.findByEmail(email).orElse(null);
        if(member == null){
            //회원가입
            log.info("join id: {}",member.getId());
        }else {
            //로그인
            log.info("not null: {}",member.getId());
        }
        return new CustomUserDetails(member, userInfo);
    }

}
