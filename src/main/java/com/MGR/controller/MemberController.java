package com.MGR.controller;

import com.MGR.dto.MemberFormDto;
import com.MGR.entity.Member;
import com.MGR.service.MailService;
import com.MGR.service.MemberService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final MailService mailService;

    @GetMapping("/login")
    public String memberLogin(){
        return "/member/loginForm";
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

    @PostMapping("/sendEmail")
    public String sendEmail(@RequestParam("email") String email) throws MessagingException {
        mailService.sendMail(email);
        System.out.println("email = " + email);
        return email;
    }

}
