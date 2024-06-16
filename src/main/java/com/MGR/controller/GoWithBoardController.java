package com.MGR.controller;

import com.MGR.dto.GoWithBoardFormDto;
import com.MGR.dto.GoWithCommentFormDto;
import com.MGR.dto.ReviewBoardForm;
import com.MGR.dto.ReviewCommentForm;
import com.MGR.entity.GoWithBoard;
import com.MGR.entity.Member;
import com.MGR.entity.ReviewBoard;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class GoWithBoardController {

    private final MemberService memberService;
    private final GoWithBoardService goWithBoardService;
    private final CategoryService categoryService;

    // 폼
    @GetMapping("/goWithBoard/create")
    public String showGoWithBoardForm(Model model) {
        addCategoryAttributes(model);
        model.addAttribute("goWithBoardFormDto", new GoWithBoardFormDto()); // 빈 폼 객체 추가
        return "board/goWith/goWithBoardForm";
    }

    // 게시글 작성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/goWithBoard/create")
    public String goWithCreate(Model model, @Valid GoWithBoardFormDto goWithBoardFormDto, BindingResult bindingResult,
                               @AuthenticationPrincipal PrincipalDetails member, HttpServletRequest request,
                               @RequestParam("goWithImgFile") List<MultipartFile> goWithImgFileList, RedirectAttributes redirectAttributes) {
        if (member == null) {
            redirectAttributes.addFlashAttribute("error", "로그인 후 이용해주세요.");
            return "member/loginForm";
        }

        if (bindingResult.hasErrors()) {
            addCategoryAttributes(model); // 카테고리 데이터 다시 추가
            System.out.println("유효성 검사 오류 = " + bindingResult.getAllErrors());
            return "board/goWith/goWithBoardForm";
        }

        Member siteUser = this.memberService.getUser(member.getName());
        System.out.println("로그인한 사용자 정보 = " + siteUser);
        // 디버깅 로그 추가
        System.out.println("ㅇㅇㅇAttraction Types: " + goWithBoardFormDto.getAttractionTypes());
        System.out.println("ㅇㅇㅇAfter Types: " + goWithBoardFormDto.getAfterTypes());
        System.out.println("ㅇㅇㅇPersonalities: " + goWithBoardFormDto.getPersonalities());

        List<MultipartFile> filteredImgFileList = goWithImgFileList.stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();
        System.out.println("filteredImgFileList 파일 = " + filteredImgFileList);

        try {
            // 게시글 생성과 이미지 저장
            Long goWithBoardId = this.goWithBoardService.createGoWithBoard(siteUser, goWithBoardFormDto.getTitle(),
                    goWithBoardFormDto.getContent(), goWithBoardFormDto.getWantDate(),
                    goWithBoardFormDto.getLocationCategory(), goWithBoardFormDto.getAgeCategory(),
                    goWithBoardFormDto.getAttractionTypes(), goWithBoardFormDto.getAfterTypes(),
                    goWithBoardFormDto.getPersonalities(), goWithImgFileList);
            System.out.println("goWithImgFileList 의 사이즈 = " + goWithImgFileList.size());
            System.out.println("goWithBoardId 가 생성되면 저장이 된거 = " + goWithBoardId);
            model.addAttribute("goWithBoardId", goWithBoardId); // ID 모델에 추가
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "새 글 생성 중 오류가 발생하였습니다.");
            System.out.println("글 생성 중 오류 발생 = " + e);
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

    // 게시글 보기
    @GetMapping(value = "/goWithBoard/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, GoWithCommentFormDto goWithCommentFormDto) {
        GoWithBoard goWithBoard = this.goWithBoardService.getGoWithBoard(id);
        goWithBoardService.saveGoWithBoard(goWithBoard);
        model.addAttribute("goWithBoard", goWithBoard);
        GoWithBoardFormDto goWithBoardFormDto = goWithBoardService.getGoWithBoardDtl(id);
        model.addAttribute("goWithBoardForm",goWithBoardFormDto);

//        프로필 보여주기에 필요한 정보 가져오기

        return "board/goWith/goWithBoardDtl";
    }


}
