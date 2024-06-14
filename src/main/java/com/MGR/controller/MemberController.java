package com.MGR.controller;

import com.MGR.dto.EventBoardFormDto;
import com.MGR.dto.MemberFormDto;
import com.MGR.entity.*;
import com.MGR.repository.MemberCouponRepository;
//import com.MGR.security.CustomUserDetails;
import com.MGR.repository.MemberCouponService;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.CouponService;
import com.MGR.service.MemberService;
import com.MGR.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final CouponService couponService;
    private final MemberCouponService memberCouponService;
    private final OrderService orderService;

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

        return "member/editForm";
    }

    @GetMapping("/changePasswordForm")
    public String oAuthMemberEdit(Model model, @AuthenticationPrincipal PrincipalDetails member){

        Member memberInfo = memberService.findByEmail(member.getUsername()).orElseThrow();
        System.out.println("memberInfo: " + memberInfo); // 로깅 추가
        model.addAttribute("memberInfo", memberInfo);

        return "member/changePasswordForm";
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

    @GetMapping({"/myCoupon", "/myCoupon/{page}"})
    public String myCoupon(Model model, @AuthenticationPrincipal PrincipalDetails member,
                         @PathVariable(value = "page", required = false) Integer page){
        if (page == null) {
            page = 0; // 페이지 값이 없을 경우 기본값을 0으로 설정
        }
        Long id = member.getId(); // 현재 인증 되어있는 사용자의 id로

        Page<MemberCoupon> paging = memberCouponService.getCouponList(page, id); // 반환
        model.addAttribute("paging", paging);

        List<Coupon> couponList = new ArrayList<>();

        for (MemberCoupon memberCoupon : paging) {
            Coupon coupon = couponService.findById(memberCoupon.getCoupon().getId());
            memberCoupon.setCoupon(coupon); // MemberCoupon 객체에 Coupon 객체 설정
            couponList.add(coupon);
        }

        model.addAttribute("couponList", couponList); // 반환

        return "member/myCoupon";
    }

    @GetMapping({"/memberList", "/memberList/{page}"})
    public String memberList(Model model,
                           @PathVariable(value = "page", required = false) Integer page){

        if (page == null) {
            page = 0; // 페이지 값이 없을 경우 기본값을 0으로 설정
        }

        Page<Member> paging = memberService.getAllMembers(page);
        model.addAttribute("paging", paging);

        List<Integer> orderCountList = new ArrayList<>();

        for (Member member : paging) {
            orderCountList.add(orderService.countByMemberId(member.getId()));
        }

        model.addAttribute("orderCountList", orderCountList); // 반환

        return "member/memberList";
    }

//    @PostMapping("/uploadProfileImage")
//    @ResponseBody
//    public String uploadProfileImage(@RequestParam("file") MultipartFile profileImgFile,
//                                     @AuthenticationPrincipal PrincipalDetails member,
//                                     RedirectAttributes redirectAttributes) {
//        try {
//            Member findMember = memberService.findById(member.getId()).orElseThrow();
//            memberService.saveProfileImg(findMember, profileImgFile);
//            redirectAttributes.addFlashAttribute("successMessage", "프로필 이미지가 성공적으로 업데이트되었습니다.");
//            return "member/editForm"; // 원래 화면 URL로 리다이렉트
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "이미지 업로드 중 오류 발생: " + e.getMessage());
//            return "member/editForm";
//        }
//    }

    @PostMapping("/uploadProfileImage")
    @ResponseBody
    public Map<String, Object> uploadProfileImage(@RequestParam("file") MultipartFile profileImgFile,
                                                  @AuthenticationPrincipal PrincipalDetails member) {
        Map<String, Object> response = new HashMap<>();
        try {
            Member findMember = memberService.findById(member.getId()).orElseThrow();
            String imageUrl = memberService.saveProfileImg(findMember, profileImgFile);
            System.out.println("imageUrl = " + imageUrl);
            response.put("success", true);
            response.put("imageUrl", imageUrl);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "이미지 업로드 중 오류 발생: " + e.getMessage());
        }
        return response;
    }
}




















