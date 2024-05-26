package com.MGR.controller;

import com.MGR.dto.QnaAnswerForm;
import com.MGR.entity.Member;
import com.MGR.entity.QnaAnswer;
import com.MGR.entity.QnaQuestion;
import com.MGR.security.CustomUserDetails;
import com.MGR.service.MemberService;
import com.MGR.service.QnaAnswerService;
import com.MGR.service.QnaQuestionService;
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

import java.security.Principal;

@RequestMapping("admin/qna/answer")
@RequiredArgsConstructor
@Controller
public class QnaAnswerController {

    private final QnaQuestionService qnaQuestionService;
    private final QnaAnswerService qnaAnswerService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Long id, @Valid QnaAnswerForm qnaAnswerForm,
                               BindingResult bindingResult,@AuthenticationPrincipal CustomUserDetails member){
        QnaQuestion question = this.qnaQuestionService.getQnaQuestion(id);
        Member siteUser = this.memberService.getUser(member.getName());
        if(bindingResult.hasErrors()){
            model.addAttribute("question", question);
            return "board/qna/question_detail";
        }
        QnaAnswer answer = this.qnaAnswerService.create(question, qnaAnswerForm.getContent(), siteUser);
        return String.format("redirect:/qna/question/detail/%s#answer_%s", answer.getQnaQuestion().getId(), answer.getId());
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify( QnaAnswerForm qnaAnswerForm, @PathVariable("id") Long id,
                                @AuthenticationPrincipal CustomUserDetails member) {
        QnaAnswer answer = this.qnaAnswerService.getAnswer(id);
        if (!answer.getAuthor().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        qnaAnswerForm.setContent(answer.getContent());
        return "board/qna/answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid QnaAnswerForm qnaAnswerForm, BindingResult bindingResult,
                               @PathVariable("id") Long id, @AuthenticationPrincipal CustomUserDetails member) {
        if (bindingResult.hasErrors()) {
            return "board/qna/answer_form";
        }
        QnaAnswer answer = this.qnaAnswerService.getAnswer(id);
        if (!answer.getAuthor().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.qnaAnswerService.modify(answer, qnaAnswerForm.getContent());
        return String.format("redirect:/qna/question/detail/%s#answer_%s", answer.getQnaQuestion().getId(), answer.getId());
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(@AuthenticationPrincipal CustomUserDetails member, @PathVariable("id") Long id) {
        QnaAnswer answer = this.qnaAnswerService.getAnswer(id);
        if (!answer.getAuthor().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.qnaAnswerService.delete(answer);
        return String.format("redirect:/qna/question/detail/%s", answer.getQnaQuestion().getId());
    }
}
