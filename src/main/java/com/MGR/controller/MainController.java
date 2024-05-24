package com.MGR.controller;

import com.MGR.dto.MemberFormDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String root() {
        return "index";
    }

    @GetMapping("/login")
    public String memberLogin(){
        return "/member/loginForm";
    }

    @GetMapping("/login/error")
    public String memberLoginError(Model model){
        model.addAttribute("loginError", "이메일 주소나 비밀번호가 일치하지 않습니다");
        return "/member/loginForm";
    }

    @GetMapping("/member/join")
    public String memberJoin(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "/member/joinForm";
    }

}
