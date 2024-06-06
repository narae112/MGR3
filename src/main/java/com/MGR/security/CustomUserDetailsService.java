//package com.MGR.security;
//
//import com.MGR.entity.Member;
//import com.MGR.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class CustomUserDetailsService implements UserDetailsService {
//
//    private final MemberRepository memberRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//
//        Optional<Member> memberOptional = memberRepository.findByEmail(email);
//        Member member = memberOptional.orElseThrow(() -> new UsernameNotFoundException(email));
//
////        return new CustomUserDetails(member);
//        return new PrincipalDetails(member);
//    }
//}
