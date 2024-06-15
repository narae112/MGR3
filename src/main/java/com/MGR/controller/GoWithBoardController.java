package com.MGR.controller;

import com.MGR.config.VerifyRecaptcha;
import com.MGR.dto.GoWithBoardFormDto;
import com.MGR.entity.Member;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.GoWithBoardService;
import com.MGR.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class GoWithBoardController {

    private final MemberService memberService;
    private final GoWithBoardService goWithBoardService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String goWithCreate(Model model, @Valid GoWithBoardFormDto goWithBoardFormDto, BindingResult bindingResult,
                               @AuthenticationPrincipal PrincipalDetails member, HttpServletRequest request,
                               @RequestParam("goWithImgFile") List<MultipartFile> goWithImgFileList, RedirectAttributes redirectAttributes) {
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        if (member == null) {
            redirectAttributes.addFlashAttribute("error", "로그인 후 이용해주세요.");
            return "member/loginForm";
        }
        try {
            boolean recaptchaVerified = VerifyRecaptcha.verify(gRecaptchaResponse);
            if (!recaptchaVerified) {
                // 리캡챠 검증 실패 처리
                bindingResult.reject("recaptcha.error", "로봇 방지 검증란을 체크해주세요");
                return "board/goWith/goWithBoardForm";
            }
        } catch (IOException e) {
            // IOException 처리
            e.printStackTrace();
            bindingResult.reject("recaptcha.error", "Error while verifying reCAPTCHA");
            return "board/goWith/goWithBoardForm";
        }

        if (bindingResult.hasErrors()) {
            return "board/goWith/goWithBoardForm";
        }

        Member siteUser = this.memberService.getUser(member.getName());
        try {
            // 게시글 생성과 이미지 저장
            Long goWithBoardId = this.goWithBoardService.createGoWithBoard(siteUser, goWithBoardFormDto.getTitle(),
                                                    goWithBoardFormDto.getContent(), goWithBoardFormDto.getWantDate(),
                                                    goWithBoardFormDto.getLocationCategory(), goWithBoardFormDto.getAgeCategory(),
                                                    goWithBoardFormDto.getAttractionTypes(), goWithBoardFormDto.getAfterTypes(),
                                                    goWithBoardFormDto.getPersonalities(), goWithImgFileList);

            model.addAttribute("goWithBoardId", goWithBoardId); // ID 모델에 추가
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "새 글 생성 중 오류가 발생하였습니다.");

            return "board/goWith/goWithBoardForm";
        }

        return "redirect:/goWith/goWithList";
    }



}
