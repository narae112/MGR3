package com.MGR.controller;

import com.MGR.dto.MemberFormDto;
import com.MGR.security.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String root(Model model) {

        return "index";
    }

    @GetMapping("/login")
    public String memberLogin(){
        return "/member/loginForm";
    }

    @GetMapping("/socialLogin") //소셜 로그인 시 비밀번호 변경하는 폼으로 이동
    public String socialLogin(Model model) {

        return "index";
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

}
