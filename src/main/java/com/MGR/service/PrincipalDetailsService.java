package com.MGR.service;

import com.MGR.oauth2.*;
import com.MGR.security.PrincipalDetails;
import com.MGR.entity.Member;
import com.MGR.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

// 시큐리티 설정에서 loginProcessingUrl("/login");
// login 요청이 오면 자동으로 UserDetails Service 타입으로 IOC 되어 있는 loadUserByUsername 함수 실행됨
@Service
@RequiredArgsConstructor
@Transactional
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername 시작, email = " + email);
        Optional<Member> memberOptional = memberRepository.findByEmail(email);

        if(memberOptional.isEmpty()){
            throw new UsernameNotFoundException(email);
        }

        Member member = memberOptional.orElseThrow(() -> new UsernameNotFoundException(email));

//        return new CustomUserDetails(member);
        return new PrincipalDetails(member);
    }

}