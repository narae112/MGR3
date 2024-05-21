package com.MGR.controller;

import com.MGR.dto.MemberFormDto;
import com.MGR.entity.Member;
import com.MGR.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String memberLogin(){
        return "/member/loginForm";
    }

    @RequestMapping("/login")
    public String login() {
        // 로그인 인증 확인 - 삭제예정
        if (isAuthenticated()) {
            System.out.println("로그인 인증됨");
            return "index";
        }
        System.out.println("로그인 인증안됨");
        return "member/login";
    }
    private boolean isAuthenticated() {
        // 로그인 인증 확인 로직 - 삭제예정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    @GetMapping("/login/error")
    public String memberLoginError(Model model){
        model.addAttribute("loginError", "이메일 주소나 비밀번호가 일치하지 않습니다");
        return "/member/loginForm";
    }


    @GetMapping("/join")
    public String memberJoin(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "/member/joinForm";
    }

    @PostMapping("/create")
    public String createMember(@Valid MemberFormDto memberFormDto,
                               Errors errors, Model model){

        if(errors.hasErrors()) {
            return "member/joinForm";
        }

        if(memberFormDto.getAuthCode().equals(memberFormDto.getCode())){
            //메일 인증번호 검증
            return "member/joinForm";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e){
            model.addAttribute("errors", e.getMessage());
            return "member/joinForm";
        }
        return "redirect:/";
    }

    @PostMapping("/emailCheck")
    @ResponseBody
    public int emailCheck(@RequestParam("email") String email){
        return memberService.emailCheck(email);
        //이메일 중복 체크 - 비동기
    }

    @PostMapping("/nicknameCheck")
    @ResponseBody
    public int nicknameCheck(@RequestParam("nickname") String nickname){
        return memberService.nicknameCheck(nickname);
        //닉네임 중복 체크 - 비동기
    }

    @PostMapping("/passwordCheck")
    @ResponseBody
    public int passwordCheck(@RequestParam("password") String password,
                             @RequestParam("password2") String password2){
        return password.equals(password2)? 0 : 1;
        //비밀번호, 비밀번호 확인 일치 확인
    }

}
