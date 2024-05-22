package com.MGR.controller;

import com.MGR.entity.Member;
import com.MGR.security.CustomUserDetails;
import com.MGR.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping("/member/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    @GetMapping("/test")
    public String adminTest(@AuthenticationPrincipal CustomUserDetails member, Model model){
        if (member == null) return "redirect:/";

        Member findMember = memberService.findById(member.getId()).orElseThrow();
        String role = findMember.getRole().toString();
        model.addAttribute("role", role);

        return "member/admin/test";
    }

}
