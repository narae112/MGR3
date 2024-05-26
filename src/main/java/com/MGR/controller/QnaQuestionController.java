package com.MGR.controller;


import com.MGR.dto.QnaQuestionForm;
import com.MGR.dto.QnaAnswerForm;
import com.MGR.entity.Member;
import com.MGR.entity.QnaQuestion;
import com.MGR.security.CustomUserDetails;
import com.MGR.service.MemberService;
import com.MGR.service.QnaQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/qna/question")
public class QnaQuestionController {
    private final QnaQuestionService questionService;
    private final MemberService memberService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        log.info("page:{}, kw:{}", page, kw);
        Page<QnaQuestion> paging = this.questionService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "board/qna/question_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, QnaAnswerForm qnaAnswerForm) {
        QnaQuestion question = this.questionService.getQnaQuestion(id);
        model.addAttribute("question", question);
        return "board/qna/question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QnaQuestionForm qnaQuestionForm) {
        return "board/qna/question_form";
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QnaQuestionForm qnaQuestionForm, BindingResult bindingResult,
                                 @AuthenticationPrincipal CustomUserDetails member) {
        if (bindingResult.hasErrors()) {
            return "board/qna/question_form";
        }
        Member siteUser = this.memberService.getUser(member.getName());
        this.questionService.create(qnaQuestionForm.getSubject(), qnaQuestionForm.getContent(), siteUser);
        return "redirect:/qna/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QnaQuestionForm qnaQuestionForm, @PathVariable("id") Long id,
                                 @AuthenticationPrincipal CustomUserDetails member) {
        QnaQuestion question = this.questionService.getQnaQuestion(id); // 수정된 부분
        if (!question.getAuthor().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        qnaQuestionForm.setSubject(question.getSubject());
        qnaQuestionForm.setContent(question.getContent());
        return "board/qna/question_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QnaQuestionForm qnaQuestionForm, BindingResult bindingResult,
                                 @AuthenticationPrincipal CustomUserDetails member,
                                 @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "board/qna/question_form";
        }
        QnaQuestion question = this.questionService.getQnaQuestion(id);
        if (!question.getAuthor().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        this.questionService.modify(question, qnaQuestionForm.getSubject(), qnaQuestionForm.getContent());

        return String.format("redirect:/qna/question/detail/%s", id);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(@AuthenticationPrincipal CustomUserDetails member,
                                 @PathVariable("id") Long id) {
        QnaQuestion question = this.questionService.getQnaQuestion(id);
        if (!question.getAuthor().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/qna/question/list";
    }

}
