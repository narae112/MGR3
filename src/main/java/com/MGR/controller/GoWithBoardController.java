package com.MGR.controller;

import com.MGR.config.ProfanityListLoader;
import com.MGR.constant.AgeCategory;
import com.MGR.constant.LocationCategory;
import com.MGR.dto.GoWithBoardFormDto;
import com.MGR.dto.GoWithCommentFormDto;
import com.MGR.entity.GoWithBoard;
import com.MGR.entity.Member;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.CategoryService;
import com.MGR.service.GoWithBoardService;
import com.MGR.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

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
    public String detail(Model model, @PathVariable("id") Long id,
                         GoWithCommentFormDto goWithCommentFormDto,
                         @AuthenticationPrincipal PrincipalDetails member) {
        GoWithBoard goWithBoard = this.goWithBoardService.getGoWithBoard(id);
        goWithBoardService.saveGoWithBoard(goWithBoard);
        GoWithBoardFormDto goWithBoardFormDto = goWithBoardService.getGoWithBoardDtl(id);

        model.addAttribute("goWithBoard", goWithBoard);
        model.addAttribute("goWithBoardForm",goWithBoardFormDto);

        return "board/goWith/goWithBoardDtl";
    }

    // 게시글 수정
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/goWithBoard/modify/{id}")
    public String goWithModify(Model model, @PathVariable("id") Long id,
                               @AuthenticationPrincipal PrincipalDetails member) {
        GoWithBoard goWithBoard = this.goWithBoardService.getGoWithBoard(id);
        if (!goWithBoard.getMember().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다");
        }

        GoWithBoardFormDto goWithBoardFormDto = new GoWithBoardFormDto();
        goWithBoardFormDto.setId(goWithBoard.getId());
        goWithBoardFormDto.setTitle(goWithBoard.getTitle());
        goWithBoardFormDto.setContent(goWithBoard.getContent());
        goWithBoardFormDto.setWantDate(goWithBoard.getWantDate());
        goWithBoardFormDto.setLocationCategory(goWithBoard.getLocationCategory());
        goWithBoardFormDto.setAgeCategory(goWithBoard.getAgeCategory());
        goWithBoardFormDto.setAttractionTypes(goWithBoard.getAttractionTypes() != null ? List.of(goWithBoard.getAttractionTypes().split(",")) : null);
        goWithBoardFormDto.setAfterTypes(goWithBoard.getAfterTypes() != null ? List.of(goWithBoard.getAfterTypes().split(",")) : null);
        goWithBoardFormDto.setPersonalities(goWithBoard.getPersonalities() != null ? List.of(goWithBoard.getPersonalities().split(",")) : null);

        model.addAttribute("goWithBoardFormDto", goWithBoardFormDto);
        addCategoryAttributes(model);
        return "board/goWith/goWithBoardForm";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/goWithBoard/modify/{id}")
    public String goWithModify(Model model, @Valid GoWithBoardFormDto goWithBoardFormDto, BindingResult bindingResult,
                               @AuthenticationPrincipal PrincipalDetails member,
                               @PathVariable("id") Long id,
                               @RequestParam("goWithImgFile") List<MultipartFile> goWithImgFileList) {

        if (bindingResult.hasErrors()) {
            addCategoryAttributes(model); // 카테고리 데이터 다시 추가
            return "board/goWith/goWithBoardForm";
        }

        GoWithBoard goWithBoard = this.goWithBoardService.getGoWithBoard(id);
        if (!goWithBoard.getMember().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다");
        }

        List<String> profanityList = ProfanityListLoader.loadProfanityList("unsafe.txt");
        for (String profanity : profanityList) {
            if (goWithBoardFormDto.getContent().contains(profanity) || goWithBoardFormDto.getTitle().contains(profanity)) {
                model.addAttribute("error", "질문에 욕설이 포함되어 있습니다. 다시 작성해주세요.");
                addCategoryAttributes(model); // 카테고리 데이터 다시 추가
                return "board/goWith/goWithBoardForm";
            }
        }

        try {
            this.goWithBoardService.modify(goWithBoard, goWithBoardFormDto.getTitle(), goWithBoardFormDto.getContent(),
                    goWithBoardFormDto.getWantDate(), goWithBoardFormDto.getLocationCategory(),
                    goWithBoardFormDto.getAgeCategory(), goWithBoardFormDto.getAttractionTypes(),
                    goWithBoardFormDto.getAfterTypes(), goWithBoardFormDto.getPersonalities(),
                    goWithImgFileList);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "게시글 수정 중 오류가 발생하였습니다");
            addCategoryAttributes(model); // 카테고리 데이터 다시 추가
            return "board/goWith/goWithBoardForm";
        }

        return String.format("redirect:/goWithBoard/detail/%s", id);
    }

    //삭제
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/goWithBoard/delete/{id}")
    public String goWithDelete(@AuthenticationPrincipal PrincipalDetails member,
                               @PathVariable("id") Long id) {
        GoWithBoard goWithBoard = this.goWithBoardService.getGoWithBoard(id);
        if (!goWithBoard.getMember().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        try {
            this.goWithBoardService.delete(goWithBoard);
        } catch (Exception e) {
            e.printStackTrace();
            // 삭제 중 에러 발생 시 에러 페이지로 리다이렉트 또는 다른 처리 방법 구현
            return "redirect:/error";
        }

        return "redirect:/board/goWith/goWithBoardList";
    }

    // 게시글 목록 조회
    @GetMapping({"/goWithBoard/list", "/goWithBoard/list/{page}"})
    public String showGoWithBoardList(Model model, @PathVariable(value = "page", required = false) Integer page) {

        int size = 6; // 페이지당 글 개수
        if (page == null || page < 0) {
            page = 0;
        }
        Page<GoWithBoardFormDto> goWithBoardsPage = goWithBoardService.getAllGoWithBoards(page, size);

        addCategoryAttributes(model); // 체크박스 데이터 추가

        // LocationCategory와 AgeCategory 추가
        model.addAttribute("locationCategories", LocationCategory.values());
        model.addAttribute("ageCategories", AgeCategory.values());

        model.addAttribute("goWithBoardsPage", goWithBoardsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", goWithBoardsPage.getTotalPages());

        return "board/goWith/goWithBoardList";
    }

    @GetMapping({"/goWithBoard/search", "/goWithBoard/search{page}"})
    public String searchGoWithBoard(Model model,
                                    @RequestParam(required = false) List<String> ageCategories,
                                    @RequestParam(required = false) List<String> locationCategories,
                                    @RequestParam(required = false) List<String> attractionTypes,
                                    @RequestParam(required = false) List<String> afterTypes,
                                    @RequestParam(required = false) List<String> personalities,
                                    @PathVariable(value = "page", required = false) Integer page){

        int size = 6; // 페이지당 글 개수
        if (page == null || page < 0) {
            page = 0;
        }

        // 서비스로부터 필터링된 결과를 받아옵니다.
        Page<GoWithBoardFormDto> filteredGoWithBoardsPage = goWithBoardService.searchGoWithBoards(ageCategories, locationCategories, attractionTypes, afterTypes, personalities, page, 6);

        addCategoryAttributes(model); // 체크박스 데이터 추가

        // LocationCategory와 AgeCategory 추가
        model.addAttribute("locationCategories", LocationCategory.values());
        model.addAttribute("ageCategories", AgeCategory.values());
        // 모델에 검색된 게시글 목록을 추가합니다.
        model.addAttribute("goWithBoardsPage", filteredGoWithBoardsPage);
        // 필요한 경우 추가 모델 속성을 설정합니다.

        // 게시글 목록 페이지로 이동합니다.
        return "board/goWith/goWithBoardList";
    }

    @GetMapping("/startChat/{id}/{nickname}")
    public String startChat(@PathVariable Long id, @PathVariable String nickname, Model model) {
        GoWithBoard goWithBoard = goWithBoardService.findById(id);
        model.addAttribute("nickname", nickname);
        model.addAttribute("title", goWithBoard.getTitle());
        return "chat/roomForm";
    }
}
