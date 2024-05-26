package com.MGR.controller;

import com.MGR.entity.Member;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/member/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    @GetMapping("/test")
    public String adminTest(@AuthenticationPrincipal PrincipalDetails member, Model model){

        Optional<Member> findMember = memberService.findByEmail(member.getUsername());
        if (findMember.isPresent()) {
            String role = findMember.get().getRole();
            System.out.println("memberTest = " + findMember);
            model.addAttribute("role", role);
            return "member/admin/test";
        }

        return "redirect:/";
    }

}
