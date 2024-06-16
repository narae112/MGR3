package com.MGR.controller;

import com.MGR.config.VerifyRecaptcha;
import com.MGR.dto.GoWithBoardFormDto;
import com.MGR.entity.Member;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.CategoryService;
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
import org.springframework.web.bind.annotation.GetMapping;
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
    private final CategoryService categoryService;

    @GetMapping("/goWithBoard/create")
    public String showGoWithBoardForm(Model model) {
        addCategoryAttributes(model);
        model.addAttribute("goWithBoardFormDto", new GoWithBoardFormDto()); // 빈 폼 객체 추가
        return "board/goWith/goWithBoardForm";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/goWithBoard/create")
    public String goWithCreate(Model model, @Valid GoWithBoardFormDto goWithBoardFormDto, BindingResult bindingResult,
                               @AuthenticationPrincipal PrincipalDetails member, HttpServletRequest request,
                               @RequestParam("goWithImgFile") List<MultipartFile> goWithImgFileList, RedirectAttributes redirectAttributes) {
        if (member == null) {
            redirectAttributes.addFlashAttribute("error", "로그인 후 이용해주세요.");
            return "member/loginForm";
        }

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        try {
            boolean recaptchaVerified = VerifyRecaptcha.verify(gRecaptchaResponse);
            if (!recaptchaVerified) {
                bindingResult.reject("recaptcha.error", "로봇 방지 검증란을 체크해주세요");
            }
        } catch (IOException e) {
            e.printStackTrace();
            bindingResult.reject("recaptcha.error", "Error while verifying reCAPTCHA");
        }

        if (bindingResult.hasErrors()) {
            addCategoryAttributes(model); // 카테고리 데이터 다시 추가
            return "board/goWith/goWithBoardForm";
        }

        Member siteUser = this.memberService.getUser(member.getName());

        // 디버깅 로그 추가
        System.out.println("Attraction Types: " + goWithBoardFormDto.getAttractionTypes());
        System.out.println("After Types: " + goWithBoardFormDto.getAfterTypes());
        System.out.println("Personalities: " + goWithBoardFormDto.getPersonalities());

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
            addCategoryAttributes(model); // 카테고리 데이터 다시 추가
            return "board/goWith/goWithBoardForm";
        }

        return "redirect:/"; // 홈 화면으로 리다이렉트
    }

    private void addCategoryAttributes(Model model) {
        model.addAttribute("attractionTypes", categoryService.getAllAttractionTypeCategories());
        model.addAttribute("afterTypes", categoryService.getAllAfterTypeCategories());
        model.addAttribute("personalities", categoryService.getAllPersonalityCategories());
    }
}
