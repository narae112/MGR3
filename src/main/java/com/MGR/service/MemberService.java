package com.MGR.service;


import com.MGR.entity.Member;
import com.MGR.exception.DataNotFoundException;
import com.MGR.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

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
        int result = member.isPresent()? 1 : 0; //DB에 동일한 이메일이 없으면 1

        return result;
    }

    public Optional<Member> findById(Long id){
        return memberRepository.findById(id);
    }
//qna question
    public Member getUser(String name) {
        Optional<Member> siteUser = this.memberRepository.findByName(name);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }


    public void updateNickname(Long id, String nickname) {
        Member member = memberRepository.findById(id).get();
        member.setNickname(nickname);
    }

    public void updatePassword(Long id, String password) {
        Member member = memberRepository.findById(id).get();
        member.setPassword(passwordEncoder.encode(password));
    }

    public List<Member> findByAllMembers() {
        return memberRepository.findAll();
    }

    public List<Member> findByAllUser() {
        //member 의 role 이 User 인 것만 찾기
        return memberRepository.findAll()
                .stream()
                .filter(member -> "ROLE_USER".equals(member.getRole()))
                .collect(Collectors.toList());
    }
     public List<Member> findMembersWithBirthdayToday() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayStr = today.format(formatter);
        return memberRepository.findByBirth(todayStr);
    }
}
