package com.MGR.controller;

import com.MGR.dto.QnAQuestionForm;
import com.MGR.dto.QnaAnswerForm;
import com.MGR.entity.Member;
import com.MGR.entity.QnaQuestion;
import com.MGR.service.MemberService;
import com.MGR.service.QnaQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/qnaQuestion")
@RequiredArgsConstructor
@Controller
public class QnaQuestionController {
    private final QnaQuestionService questionService;
    private final MemberService memberService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<QnaQuestion> paging = this.questionService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "board/qna/question_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, QnaAnswerForm qnaAnswerForm) {
        QnaQuestion question = this.questionService.getQnaBoard(id);
        model.addAttribute("question", question);
        return "board/qna/question_detail";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QnAQuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "board/qna/question_form";
        }
        Member siteUser = this.memberService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/qnaQuestion/list";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QnAQuestionForm questionForm, @PathVariable("id") Long id, Principal principal) {
        QnaQuestion question = this.questionService.getQnaBoard(id);
        if (!question.getAuthor().getName().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "board/qna/question_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QnAQuestionForm questionForm, BindingResult bindingResult, Principal principal,
                                 @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "board/qna/question_form";
        }
        QnaQuestion question = this.questionService.getQnaBoard(id);
        if (!question.getAuthor().getName().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/qnaQuestion/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Long id) {
        QnaQuestion question = this.questionService.getQnaBoard(id);
        if (!question.getAuthor().getName().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/qnaQuestion";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Long id) {
        QnaQuestion question = this.questionService.getQnaBoard(id);
        Member siteUser = this.memberService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/qnaQuestion/detail/%s", id);
    }
}
