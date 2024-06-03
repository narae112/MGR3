package com.MGR.security;

import com.MGR.dto.MemberFormDto;
import com.MGR.entity.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    //로그인을 시도할 때 실행
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        MemberFormDto memberDto = null;
        try {
            memberDto = objectMapper.readValue(request.getInputStream(), MemberFormDto.class); //request로 들어온 JSON 형식을 MemberDto로 가져옴
        } catch (Exception e) {
//            e.printStackTrace();
        }

        //토큰 생성
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(memberDto.getEmail(), memberDto.getPassword());
        System.out.println("JWT token = " + token);
        //authenticationManager의 authenticate 메소드 실행.
        //authenticationManager는 처리할 수 있는 authenticationProvider를 찾아서 authenticationProvider의 authenticate 메소드 실행.
        Authentication authenticate = authenticationManager.authenticate(token);
        //Authentication 객체 반환. 세션에 저장됨.
        log.info("정보 attemptAuthentication");
        log.debug("디버그 attemptAuthentication");
        log.error("에러 attemptAuthentication");

        return authenticate;

    }
    //인증을 성공하면 실행
    //response Authorization header 에 jwt 를 담아서 보내줌
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principal = (PrincipalDetails) authResult.getPrincipal();
        Member member = principal.getMember();
        String jwt = jwtUtil.createJwt(member.getEmail(), member.getId());
        response.setHeader("Authorization", jwt);
    }
}