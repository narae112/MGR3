package com.MGR.controller;


import com.MGR.config.ProfanityListLoader;
import com.MGR.dto.QnaQuestionForm;
import com.MGR.dto.QnaAnswerForm;
import com.MGR.entity.Member;
import com.MGR.entity.QnaQuestion;
import com.MGR.security.CustomUserDetails;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.MemberService;
import com.MGR.service.QnaQuestionService;
import jakarta.servlet.http.HttpServletRequest;
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
import com.MGR.config.VerifyRecaptcha;

import java.io.IOException;
import java.util.List;

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
    public String questionCreate(Model model, @Valid QnaQuestionForm qnaQuestionForm, BindingResult bindingResult,
                                 @AuthenticationPrincipal PrincipalDetails member, HttpServletRequest request) {
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        try {
            boolean recaptchaVerified = VerifyRecaptcha.verify(gRecaptchaResponse);
            if (!recaptchaVerified) {
                // 리캡챠 검증 실패 처리
                bindingResult.reject("recaptcha.error", "체크표시를 해주세요");
                return "board/qna/question_form";
            }
        } catch (IOException e) {
            // IOException 처리
            e.printStackTrace();
            bindingResult.reject("recaptcha.error", "Error while verifying reCAPTCHA");
            return "board/qna/question_form";
        }

        if (bindingResult.hasErrors()) {
            return "board/qna/question_form";
        }
        List<String> profanityList = ProfanityListLoader.loadProfanityList("unsafe.txt");
        for (String profanity : profanityList) {
            if (qnaQuestionForm.getContent().contains(profanity) || qnaQuestionForm.getSubject().contains(profanity)) {
                model.addAttribute("error", "질문에 욕설이 포함되어 있습니다. 다시 작성해주세요.");
                return "board/qna/question_form";
            }
        }
        Member siteUser = this.memberService.getUser(member.getName());
        this.questionService.create(qnaQuestionForm.getSubject(), qnaQuestionForm.getContent(), siteUser);
        return "redirect:/qna/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QnaQuestionForm qnaQuestionForm, @PathVariable("id") Long id,
                                 @AuthenticationPrincipal   PrincipalDetails member) {
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
    public String questionModify(Model model, @Valid QnaQuestionForm qnaQuestionForm, BindingResult bindingResult,

                                 @AuthenticationPrincipal  PrincipalDetails member,
                                 @PathVariable("id") Long id, HttpServletRequest request) {
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        try {
            boolean recaptchaVerified = VerifyRecaptcha.verify(gRecaptchaResponse);
            if (!recaptchaVerified) {
                // 리캡챠 검증 실패 처리
                bindingResult.reject("recaptcha.error", "체크표시를 해주세요");
                return "board/qna/question_form";
            }
        } catch (IOException e) {
            // IOException 처리
            e.printStackTrace();
            bindingResult.reject("recaptcha.error", "Error while verifying reCAPTCHA");
            return "board/qna/question_form";
        }

        if (bindingResult.hasErrors()) {
            return "board/qna/question_form";
        }
        QnaQuestion question = this.questionService.getQnaQuestion(id);
        if (!question.getAuthor().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        List<String> profanityList = ProfanityListLoader.loadProfanityList("unsafe.txt");
        for (String profanity : profanityList) {
            if (qnaQuestionForm.getContent().contains(profanity) || qnaQuestionForm.getSubject().contains(profanity)) {
                model.addAttribute("error", "질문에 욕설이 포함되어 있습니다. 다시 작성해주세요.");
                return "board/qna/question_form";
            }
        }
        this.questionService.modify(question, qnaQuestionForm.getSubject(), qnaQuestionForm.getContent());

        return String.format("redirect:/qna/question/detail/%s", id);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(@AuthenticationPrincipal  PrincipalDetails member,
                                 @PathVariable("id") Long id) {
        QnaQuestion question = this.questionService.getQnaQuestion(id);
        if (!question.getAuthor().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/qna/question/list";
    }

}
