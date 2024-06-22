package com.MGR.controller;

import com.MGR.config.ProfanityListLoader;
import com.MGR.dto.ReviewCommentForm;
import com.MGR.entity.Member;
import com.MGR.entity.ReviewBoard;
import com.MGR.entity.ReviewComment;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.MemberService;
import com.MGR.service.ReviewCommentService;
import com.MGR.service.ReviewBoardService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequestMapping("review/comment")
@RequiredArgsConstructor
@Controller
public class ReviewCommentController {

    private final ReviewBoardService reviewBoardService;
    private final ReviewCommentService reviewCommentService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createReview(Model model, @PathVariable("id") Long id, @Valid ReviewCommentForm reviewCommentForm,
                               BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails member,
                               RedirectAttributes redirectAttributes) {
        ReviewBoard reviewBoard = this.reviewBoardService.getReviewBoard(id);

        // 멤버가 null인지 확인
        if (member == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요한 서비스입니다.");
            return "member/loginForm";
        }

        Member siteUser = this.memberService.getUser(member.getEmail());

        // 욕설 필터링
        List<String> profanityList = ProfanityListLoader.loadProfanityList("unsafe.txt");
        for (String profanity : profanityList) {
            if (reviewCommentForm.getContent().contains(profanity)) {
                // 욕설이 포함되어 있으면 처리 거부
                model.addAttribute("error", "답변에 욕설이 포함되어 있습니다. 다시 작성해주세요.");
                model.addAttribute("reviewBoard", reviewBoard);
                return "board/review/board_detail";
            }
        }

        // 폼 유효성 검사
        if (bindingResult.hasErrors()) {
            model.addAttribute("reviewBoard", reviewBoard);
            return "board/review/board_detail";
        }

        // 답변 생성 시도
        ReviewComment reviewComment = this.reviewCommentService.create(reviewBoard, reviewCommentForm.getContent(), siteUser);
        return String.format("redirect:/review/board/detail/%s#comment_%s", reviewComment.getReviewBoard().getId(), reviewComment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String commentModify(ReviewCommentForm reviewCommentForm, @PathVariable("id") Long id,
                               @AuthenticationPrincipal PrincipalDetails member) {
        ReviewComment reviewComment = this.reviewCommentService.getComment(id);
        if (!reviewComment.getAuthor().getEmail().equals(member.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        reviewComment.setContent(reviewComment.getContent());
        return "board/review/comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String commentModify(Model model, @Valid ReviewCommentForm reviewCommentForm, BindingResult bindingResult,
                               @PathVariable("id") Long id, @AuthenticationPrincipal PrincipalDetails member) {

        List<String> profanityList = ProfanityListLoader.loadProfanityList("unsafe.txt");
        for (String profanity : profanityList) {
            if (reviewCommentForm.getContent().contains(profanity)) {
                // 욕설이 포함되어 있으면 처리 거부
                model.addAttribute("error", "답변에 욕설이 포함되어 있습니다. 다시 작성해주세요.");
                return "board/review/comment_form";
            }
        }
        if (bindingResult.hasErrors()) {
            return "board/review/comment_form";
        }
        ReviewComment reviewComment = this. reviewCommentService.getComment(id);
        if (!reviewComment.getAuthor().getEmail().equals(member.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this. reviewCommentService.modify(reviewComment, reviewCommentForm.getContent());
        return String.format("redirect:/review/board/detail/%s#comment_%s", reviewComment.getReviewBoard().getId(), reviewComment.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String reviewDelete(@AuthenticationPrincipal PrincipalDetails member, @PathVariable("id") Long id) {
        ReviewComment reviewComment = this. reviewCommentService.getComment(id);
        if (!reviewComment.getAuthor().getEmail().equals(member.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this. reviewCommentService.delete(reviewComment);
        return String.format("redirect:/review/board/detail/%s", reviewComment.getReviewBoard().getId());
    }
}
