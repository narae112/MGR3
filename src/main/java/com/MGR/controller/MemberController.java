package com.MGR.controller;

import com.MGR.dto.EventBoardFormDto;
import com.MGR.dto.MemberFormDto;
import com.MGR.entity.Member;
import com.MGR.security.CustomUserDetails;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
    public String memberEdit(Model model, @AuthenticationPrincipal PrincipalDetails member){

        Member memberInfo = memberService.findByEmail(member.getUsername()).orElseThrow();
        System.out.println("memberInfo: " + memberInfo); // 로깅 추가
        model.addAttribute("memberInfo", memberInfo);

        return "/member/editForm";
    }

    @PostMapping("/editNickname/{id}")
    public String memberInfoEditNickname(@PathVariable("id") Long id,
                                         @AuthenticationPrincipal PrincipalDetails member,
                                         @Valid MemberFormDto memberInfo,
                                         BindingResult result, Model model){

        if(!StringUtils.hasText(memberInfo.getNickname())) {
            model.addAttribute("stringError", "닉네임을 입력하세요");
            return "member/edit";
        }

        try {
            Optional<Member> findMember = memberService.findByEmail(member.getUsername());
            memberService.updateNickname(id,memberInfo.getNickname());
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/edit";
        }

        return "redirect:/";
    }

    @PostMapping("/editPassword/{id}")
    public String memberInfoEditPassword(@PathVariable("id") Long id,
                                 @AuthenticationPrincipal PrincipalDetails member,
                                 @Valid MemberFormDto memberInfo,
                                 BindingResult result, Model model){

//        if(!StringUtils.hasText(memberInfo.getPassword())) {
//            model.addAttribute("errorMessage", "비밀번호를 입력하세요");
//            return "/member/edit";
//        }

        if(result.hasErrors()){

            return "redirect:/member/edit";
        }

        try {
            Optional<Member> findMember = memberService.findByEmail(member.getUsername());
            memberService.updatePassword(id, memberInfo.getPassword());
        }catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/member/edit";
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
        //비밀번호, 비밀번호2 일치 확인
    }

    @GetMapping("/verifyPassword")
    @ResponseBody
    public int verifyPassword(@RequestParam("verifyPassword") String password,
                              @AuthenticationPrincipal PrincipalDetails member){
        return passwordEncoder.matches(password,member.getPassword())? 0 : 1;
        //회원정보 변경 전 비밀번호 인증
    }

}
