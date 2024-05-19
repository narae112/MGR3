//package com.MGR.service;
//
//import com.MGR.Dto.MemberFormDto;
//import com.MGR.entity.Member;
//import com.MGR.repository.MemberRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class MemberServiceTest {
//
//    @Autowired MemberRepository memberRepository;
//    @Autowired MemberService memberService;
//    @Autowired PasswordEncoder passwordEncoder;
//
//    @Test
//    void 회원가입() {
//        MemberFormDto memberFormDto = new MemberFormDto();
//        memberFormDto.setEmail("test@naver.com");
//        memberFormDto.setPassword("test");
//
//        Member member = Member.createMember(memberFormDto, passwordEncoder);
//        memberService.saveMember(member);
//
//        Member result = memberRepository.findByEmail(member.getEmail());
//
//        Assertions.assertThat(result).isEqualTo(member);
//    }
//}