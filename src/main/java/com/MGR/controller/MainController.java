package com.MGR.controller;

import com.MGR.dto.MemberFormDto;
import com.MGR.dto.ReviewBoardForm;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.ReviewBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MainController {
    @Autowired
    private ReviewBoardService reviewBoardService;

    @GetMapping("/")
    public String root(Model model) {
        List<ReviewBoardForm> topRatedReviews = reviewBoardService.getTopRatedReviews();
        model.addAttribute("topRatedReviews", topRatedReviews);
        return "index";
    }

    @GetMapping("/login")
    public String memberLogin(){
        return "member/loginForm";
    }

    @GetMapping("/login/error")
    public String memberLoginError(Model model){
        model.addAttribute("loginError", "이메일 주소나 비밀번호가 일치하지 않습니다");
        return "member/loginForm";
    }

    @GetMapping("/join")
    public String memberJoin(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/joinForm";
    }

}
