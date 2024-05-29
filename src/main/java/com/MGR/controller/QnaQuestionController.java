package com.MGR.controller;


import com.MGR.config.ProfanityListLoader;
import com.MGR.dto.QnaQuestionForm;
import com.MGR.dto.QnaAnswerForm;
import com.MGR.entity.Member;
import com.MGR.entity.QnaQuestion;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.FileService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.MGR.config.VerifyRecaptcha;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/qna/question")
public class QnaQuestionController {
    private final QnaQuestionService questionService;
    private final MemberService memberService;
    private final FileService fileService;
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
        int count = question.viewCount();
        question.setCount(count);
        questionService.saveQuestion(question);
        model.addAttribute("question", question);
        QnaQuestionForm qnaQuestionForm = questionService.getQuestionDtl(id);
        model.addAttribute("qnaQuestionForm",qnaQuestionForm);
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
                                 @AuthenticationPrincipal PrincipalDetails member, HttpServletRequest request,
                                 @RequestParam("reviewImgFile") List<MultipartFile> reviewImgFileList, RedirectAttributes redirectAttributes) {
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        if (member == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요한 서비스입니다.");
            return "member/loginForm";
        }
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
        try {
            // 질문 생성과 이미지 저장
            Long questionId = this.questionService.createQuestion(qnaQuestionForm.getSubject(), qnaQuestionForm.getContent(), siteUser, reviewImgFileList);
            model.addAttribute("questionId", questionId); // 질문 ID 모델에 추가
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "질문 생성 중 오류가 발생하였습니다.");
            return "board/qna/question_form";
        }

        return "redirect:/qna/question/list";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(Model model, @PathVariable("id") Long id,
                                 @AuthenticationPrincipal PrincipalDetails member) {
        QnaQuestion question = this.questionService.getQnaQuestion(id);
        if (!question.getAuthor().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        QnaQuestionForm qnaQuestionForm = new QnaQuestionForm();
        qnaQuestionForm.setId(question.getId()); // 수정된 부분
        qnaQuestionForm.setSubject(question.getSubject());
        qnaQuestionForm.setContent(question.getContent());

        model.addAttribute("qnaQuestionForm", qnaQuestionForm);
        return "board/qna/question_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(Model model, @Valid QnaQuestionForm qnaQuestionForm, BindingResult bindingResult,
                                 @AuthenticationPrincipal PrincipalDetails member,
                                 @PathVariable("id") Long id, HttpServletRequest request,
                                 @RequestParam("reviewImgFile") List<MultipartFile> reviewImgFileList) {
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

        try {
            this.questionService.modify(question, qnaQuestionForm.getSubject(), qnaQuestionForm.getContent(), reviewImgFileList);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "질문 수정 중 오류가 발생하였습니다.");
            return "board/qna/question_form";
        }

        return String.format("redirect:/qna/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(@AuthenticationPrincipal PrincipalDetails member,
                                 @PathVariable("id") Long id) {
        QnaQuestion question = this.questionService.getQnaQuestion(id);
        if (!question.getAuthor().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        try {
            this.questionService.delete(question);
        } catch (Exception e) {
            e.printStackTrace();
            // 삭제 중 에러 발생 시 에러 페이지로 리다이렉트 또는 다른 처리 방법 구현
            return "redirect:/error";
        }

        return "redirect:/qna/question/list";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(@AuthenticationPrincipal PrincipalDetails member,
                               @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (member == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요한 서비스입니다.");
            return "member/loginForm";
        }

        try {
            QnaQuestion question = this.questionService.getQnaQuestion(id);
            Member siteUser = this.memberService.getUser(member.getName());
            this.questionService.vote(question, siteUser);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "오류가 발생했습니다. 다시 시도해주세요.");
            return String.format("redirect:/qna/question/detail/%s", id);
        }

        return String.format("redirect:/qna/question/detail/%s", id);
    }



}
