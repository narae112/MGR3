package com.MGR.service;

import com.MGR.entity.Member;
import com.MGR.oauth2.*;
import com.MGR.repository.MemberRepository;
import com.MGR.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final PasswordEncoder encoder;
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User = " + oAuth2User.getAttributes());

        OAuth2MemberInfo memberInfo = null;

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("registrationId = " + registrationId);

        switch (registrationId) {
            case "google":
                memberInfo = new GoogleMemberInfo(oAuth2User.getAttributes());
                break;
            case "naver":
                memberInfo = new NaverMemberInfo((Map) oAuth2User.getAttributes().get("response"));
                break;
            case "github":
                memberInfo = new GithubMemberInfo(oAuth2User.getAttributes());
                break;
            case "kakao":
                memberInfo = new KakaoMemberInfo(oAuth2User.getAttributes());
                break;
            default:
                throw new OAuth2AuthenticationException("OAuth2 로그인 실패 = " + registrationId);
        }

        String provider = memberInfo.getProvider();
        String providerId = memberInfo.getProviderId();
        String oauth2Id = provider + "_" + providerId; //중복이 발생하지 않도록 provider 와 providerId를 조합
        String username = memberInfo.getName();
        String profileImgUrl = memberInfo.getProfileImgUrl();
        String email = memberInfo.getEmail();
        String nickname = memberInfo.getNickname() + "_" + oauth2Id;
        String role = "ROLE_USER"; //일반 유저

        if (registrationId.equals("github") && (email == null || email.isEmpty())) {
            String accessToken = userRequest.getAccessToken().getTokenValue();
            email = getGithubEmail(accessToken);
        }

        Optional<Member> findMember = memberRepository.findByEmail(email);
        Member member=null;
        if (findMember.isEmpty()) { //중복 이메일을 찾지 못했다면
            member = Member.builder()
                    .oauth2Id(oauth2Id)
                    .name(username)
                    .nickname(nickname)
                    .email(email)
                    .password(encoder.encode("2024"))
                    .role(role)
                    .profileImgUrl(profileImgUrl)
                    .provider(provider)
                    .providerId(providerId).build();
            System.out.println(member.toString());
            memberRepository.save(member);
        }
        else{
            System.out.println("찾았다");
            member=findMember.get();
        }
        return new PrincipalDetails(member);
    }

    private String getGithubEmail(String accessToken) {
        System.out.println("getGithubEmail 시작");
        String email = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.github.com/user/emails";

            HttpHeaders headers = new HttpHeaders();

            headers.set("Authorization", "token " + accessToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            System.out.println("ㅇㅇㅇㅇAccess Token: " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<List<Map<String, Object>>> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity,
                            new ParameterizedTypeReference<>() {});

            System.out.println("ㅇㅇㅇㅇㅇㅇResponse Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            List<Map<String, Object>> emails = response.getBody();

            if (emails != null) {
                for (Map<String, Object> emailMap : emails) {
                    if ((boolean) emailMap.get("primary")) {
                        email = (String) emailMap.get("email");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("예외처리 = " + e);
        }
        return email;
    }
}
