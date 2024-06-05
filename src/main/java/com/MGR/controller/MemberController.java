package com.MGR.controller;

import com.MGR.dto.EventBoardFormDto;
import com.MGR.dto.MemberFormDto;
import com.MGR.entity.Coupon;
import com.MGR.entity.Member;
import com.MGR.entity.MemberCoupon;
import com.MGR.repository.MemberCouponRepository;
import com.MGR.security.CustomUserDetails;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.CouponService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MemberCouponRepository memberCouponRepository;
    private final CouponService couponService;

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
            return "member/editForm";
        }

        try {
            Optional<Member> findMember = memberService.findByEmail(member.getUsername());
            memberService.updateNickname(id,memberInfo.getNickname());
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/editForm";
        }

        return "redirect:/";
    }

    @PostMapping("/editPassword/{id}")
    public String memberInfoEditPassword(@PathVariable("id") Long id,
                                 @AuthenticationPrincipal PrincipalDetails member,
                                 Member memberInfo,
                                 Model model){

        if(memberInfo.getPassword().length() < 8 || memberInfo.getPassword().length() > 16) {
            model.addAttribute("errorMessage", "비밀번호를 8자 이상 16자 이하로 입력하세요");
            System.out.println("StringUtils 에러");
            return "member/editForm";
        }

        try {
            Optional<Member> findMember = memberService.findByEmail(member.getUsername());
            memberService.updatePassword(id, memberInfo.getPassword());
        }catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            System.out.println("IllegalStateException 에러" + e.getMessage());
            return "member/editForm";
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

    @GetMapping("/myPage")
    public String myPage(Model model, @AuthenticationPrincipal PrincipalDetails member){

        Long memberId = member.getId(); // 현재 인증 되어있는 사용자의 id로

        List<MemberCoupon> memberCouponList = memberCouponRepository.findAllByMemberId(memberId);//member coupon 을 전부 찾아서
        model.addAttribute("memberCouponList", memberCouponList); // 반환

        List<Coupon> couponList = new ArrayList<>();
        for (MemberCoupon memberCoupon : memberCouponList) {
            Coupon coupon = couponService.findById(memberCoupon.getCoupon().getId());
            memberCoupon.setCoupon(coupon); // MemberCoupon 객체에 Coupon 객체 설정
            couponList.add(coupon);
            // member coupon 쿠폰의 id로 coupon 찾아서
        }

        model.addAttribute("couponList", couponList); // 반환

        return "member/myPage";
    }

}




















