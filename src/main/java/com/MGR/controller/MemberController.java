package com.MGR.controller;

import com.MGR.dto.EventBoardFormDto;
import com.MGR.dto.MemberFormDto;
import com.MGR.entity.Member;
import com.MGR.security.CustomUserDetails;
import com.MGR.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public String createMember(@Valid MemberFormDto memberFormDto,
                               BindingResult result, Model model){


        if(memberFormDto.getAuthCode().equals(memberFormDto.getCode())){
            //메일 인증번호 검증
            return "member/joinForm";
        }
        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e){
            model.addAttribute("errors2", e.getMessage());
            return "member/joinForm";
        }
        return "redirect:/";
    }

    @GetMapping("/edit")
    public String memberEdit(Model model, @AuthenticationPrincipal CustomUserDetails member){

        Member memberInfo = memberService.findByEmail(member.getUsername()).orElseThrow();
        model.addAttribute("memberInfo", memberInfo);

        return "/member/editForm";
    }

    @PostMapping("/editNickname/{id}")
    public String memberInfoEditNickname(@PathVariable("id") Long id,
                                 @AuthenticationPrincipal CustomUserDetails member,
                                 @Valid MemberFormDto memberInfo,Errors errors,
                                         Model model){

        Optional<Member> findMember = memberService.findByEmail(member.getUsername());

        if(errors.hasErrors()) {
            model.addAttribute("errors", errors);
            return "/member/edit";
        }

        memberService.updateNickname(id,memberInfo.getNickname());

        return "redirect:/member/edit";
    }

    @PostMapping("/editPassword/{id}")
    public String memberInfoEditPassword(@PathVariable("id") Long id,
                                 @AuthenticationPrincipal CustomUserDetails member,
                                 MemberFormDto memberInfo){

        Optional<Member> findMember = memberService.findByEmail(member.getUsername());
        memberService.updatePassword(id,memberInfo.getPassword());

        return "redirect:/member/edit";
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
        //비밀번호, 비밀번호2 일치 확인
    }

    @GetMapping("/verifyPassword")
    @ResponseBody
    public int verifyPassword(@RequestParam("verifyPassword") String password,
                              @AuthenticationPrincipal CustomUserDetails member){
        return passwordEncoder.matches(password,member.getPassword())? 0 : 1;
        //회원정보 변경 전 비밀번호 인증
    }

}
