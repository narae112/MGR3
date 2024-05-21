package com.MGR.service;

import com.MGR.entity.Member;
import com.MGR.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService{

    private final MemberRepository memberRepository;

    //멤버 저장
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    //닉네임 중복체크
    public int nicknameCheck(String nickname){
        Optional<Member> member = memberRepository.findByNickname(nickname);
        int result = member.isPresent()? 1 : 0; //1이면 중복

        return result;
    }

    //이메일 중복 체크
    public int emailCheck(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        int result = member.isPresent()? 1 : 0;

        return result;
    }
}
