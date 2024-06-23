package com.MGR.controller;

import com.MGR.config.ProfanityListLoader;
import com.MGR.dto.GoWithCommentFormDto;
import com.MGR.entity.*;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.GoWithBoardService;
import com.MGR.service.GoWithCommentService;
import com.MGR.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@RequiredArgsConstructor
@Controller
public class GoWithCommentController {

    private final GoWithBoardService goWithBoardService;
    private final GoWithCommentService goWithCommentService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/goWith/comment/create/{id}")
    public String createGoWithComment(Model model, @PathVariable("id") Long id, @Valid GoWithCommentFormDto goWithCommentFormDto,
                               BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails member,
                               RedirectAttributes redirectAttributes) {
        GoWithBoard goWithBoard = this.goWithBoardService.getGoWithBoard(id);

        // 멤버가 null인지 확인
        if (member == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요한 서비스입니다.");
            return "member/loginForm";
        }

        Member siteUser = this.memberService.getUser(member.getEmail());

        // 욕설 필터링
        List<String> profanityList = ProfanityListLoader.loadProfanityList("unsafe.txt");
        for (String profanity : profanityList) {
            if (goWithCommentFormDto.getContent().contains(profanity)) {
                // 욕설이 포함되어 있으면 처리 거부
                model.addAttribute("error", "답변에 욕설이 포함되어 있습니다. 다시 작성해주세요.");
                model.addAttribute("goWithBoard", goWithBoard);
                return "board/goWith/goWithBoardDtl";
            }
        }

        // 폼 유효성 검사
        if (bindingResult.hasErrors()) {
            model.addAttribute("goWithBoard", goWithBoard);
            return "board/goWith/goWithBoardDtl";
        }

        // 답변 생성 시도
        GoWithComment goWithComment = this.goWithCommentService.create(goWithBoard, goWithCommentFormDto.getContent(), siteUser);
        return String.format("redirect:/goWithBoard/detail/%s#comment_%s", goWithComment.getGoWithBoard().getId(), goWithComment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/goWith/comment/modify/{id}")
    public String commentModify(GoWithCommentFormDto goWithCommentFormDto, @PathVariable("id") Long id,
                                @AuthenticationPrincipal PrincipalDetails member) {
        GoWithComment goWithComment = this.goWithCommentService.getComment(id);
        if (!goWithComment.getMember().getEmail().equals(member.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        goWithComment.setContent(goWithComment.getContent());
        return "board/goWith/goWithCommentForm";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/goWith/comment/modify/{id}")
    public String commentModify(Model model, @Valid GoWithCommentFormDto goWithCommentFormDto, BindingResult bindingResult,
                                @PathVariable("id") Long id, @AuthenticationPrincipal PrincipalDetails member) {

        List<String> profanityList = ProfanityListLoader.loadProfanityList("unsafe.txt");
        for (String profanity : profanityList) {
            if (goWithCommentFormDto.getContent().contains(profanity)) {
                // 욕설이 포함되어 있으면 처리 거부
                model.addAttribute("error", "답변에 욕설이 포함되어 있습니다. 다시 작성해주세요.");
                return "board/goWith/goWithCommentForm";
            }
        }
        if (bindingResult.hasErrors()) {
            return "board/goWith/goWithCommentForm";
        }
        GoWithComment goWithComment = this.goWithCommentService.getComment(id);
        if (!goWithComment.getMember().getEmail().equals(member.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this. goWithCommentService.modify(goWithComment, goWithComment.getContent());
        return String.format("redirect:/goWithBoard/detail/%s#comment_%s", goWithComment.getGoWithBoard().getId(), goWithComment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/goWith/comment/delete/{id}")
    public String goWithCommentDelete(@AuthenticationPrincipal PrincipalDetails member, @PathVariable("id") Long id) {
        GoWithComment goWithComment = this.goWithCommentService.getComment(id);
        if (!goWithComment.getMember().getEmail().equals(member.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.goWithCommentService.delete(goWithComment);
        return String.format("redirect:/goWithBoard/detail/%s", goWithComment.getGoWithBoard().getId());
    }
}
