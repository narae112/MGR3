package com.MGR.controller;


import com.MGR.config.ProfanityListLoader;
import com.MGR.dto.OrderCheckForm;
import com.MGR.dto.ReviewBoardForm;
import com.MGR.dto.ReviewCommentForm;
import com.MGR.entity.Member;
import com.MGR.entity.Order;
import com.MGR.entity.ReviewBoard;
import com.MGR.repository.ReviewBoardRepository;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.FileService;
import com.MGR.service.MemberService;
import com.MGR.service.OrderService;
import com.MGR.service.ReviewBoardService;
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
@RequestMapping("/review/board")
public class ReviewBoardController {
    private final ReviewBoardService reviewBoardService;
    private final MemberService memberService;
    private final FileService fileService;
    private final OrderService orderService;
    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "sort", defaultValue = "date") String sort) {
        // 페이징된 리뷰 게시글 목록 가져오기
        Page<ReviewBoard> paging = this.reviewBoardService.getList(page, kw, sort);

        // 전체 리뷰 게시글의 정보 가져오기 (이미지 포함)
        List<ReviewBoardForm> reviewBoardForms = this.reviewBoardService.getReviewBoardForms();

        // 페이징 정보와 리뷰 게시글 폼을 모델에 추가
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        model.addAttribute("sort", sort);
        model.addAttribute("reviewBoardForms", reviewBoardForms);

        return "board/review/board_list";
    }
    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, ReviewCommentForm reviewCommentForm) {
        ReviewBoard reviewBoard = this.reviewBoardService.getReviewBoard(id);
        int count = reviewBoard.viewCount();
        reviewBoard.setCount(count);
        reviewBoardService.saveReviewBoard(reviewBoard);
        model.addAttribute("reviewBoard", reviewBoard);
        ReviewBoardForm reviewBoardForm = reviewBoardService.getReviewBoardDtl(id);
        model.addAttribute("reviewBoardForm",reviewBoardForm);
        return "board/review/board_detail";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/orderCheck")
    public String orderCheck(OrderCheckForm orderCheckForm) {
        return "board/review/orderCheck_form";
    }


    @PostMapping("/orderCheck")
    public String orderCheck(@RequestParam("orderNum") String orderNum, RedirectAttributes redirectAttributes, Model model) {
        // 주문번호를 기준으로 주문을 조회합니다.
        Order order = orderService.findOrderByOrderNum(orderNum);

        // 주문번호에 해당하는 주문이 없는 경우
        if (order == null) {
            model.addAttribute("error", "주문을 찾을 수 없습니다.");
            model.addAttribute("orderCheckForm", new OrderCheckForm()); // orderCheckForm 객체를 모델에 추가
            return "board/review/orderCheck_form"; // 에러 페이지로 이동하거나 적절한 처리를 합니다.
        }

        // 주문번호가 일치하면 리뷰 작성 페이지로 이동합니다.
        return "redirect:/review/board/create"; // 리뷰 작성 페이지로 리다이렉트합니다.
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String reviewCreate(ReviewBoardForm reviewBoardForm) {
        return "board/review/board_form";
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String reviewCreate(Model model, @Valid ReviewBoardForm reviewBoardForm, BindingResult bindingResult,
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
                return "board/review/board_form";
            }
        } catch (IOException e) {
            // IOException 처리
            e.printStackTrace();
            bindingResult.reject("recaptcha.error", "Error while verifying reCAPTCHA");
            return "board/review/board_form";
        }

        if (bindingResult.hasErrors()) {
            return "board/review/board_form";
        }

        List<String> profanityList = ProfanityListLoader.loadProfanityList("unsafe.txt");
        for (String profanity : profanityList) {
            if (reviewBoardForm.getContent().contains(profanity) || reviewBoardForm.getSubject().contains(profanity)) {
                model.addAttribute("error", "질문에 욕설이 포함되어 있습니다. 다시 작성해주세요.");
                return "board/review/board_form";
            }
        }

        Member siteUser = this.memberService.getUser(member.getName());
        try {
            // 질문 생성과 이미지 저장
            Long reviewBoardId = this.reviewBoardService.createReviewBoard(reviewBoardForm.getSubject(), reviewBoardForm.getContent(), siteUser, reviewImgFileList);
            model.addAttribute("reviewBoardId", reviewBoardId); // 질문 ID 모델에 추가
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "질문 생성 중 오류가 발생하였습니다.");
            return "board/review/board_form";
        }

        return "redirect:/review/board/list";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String reviewModify(Model model, @PathVariable("id") Long id,
                                 @AuthenticationPrincipal PrincipalDetails member) {
        ReviewBoard reviewBoard = this.reviewBoardService.getReviewBoard(id);
        if (!reviewBoard.getAuthor().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        ReviewBoardForm reviewBoardForm = new ReviewBoardForm();
        reviewBoardForm.setId(reviewBoard.getId()); // 수정된 부분
        reviewBoardForm.setSubject(reviewBoard.getSubject());
        reviewBoardForm.setContent(reviewBoard.getContent());

        model.addAttribute("reviewBoardForm", reviewBoardForm);
        return "board/review/board_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String reviewModify(Model model, @Valid ReviewBoardForm reviewBoardForm, BindingResult bindingResult,
                                 @AuthenticationPrincipal PrincipalDetails member,
                                 @PathVariable("id") Long id, HttpServletRequest request,
                                 @RequestParam("reviewImgFile") List<MultipartFile> reviewImgFileList) {
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        try {
            boolean recaptchaVerified = VerifyRecaptcha.verify(gRecaptchaResponse);
            if (!recaptchaVerified) {
                // 리캡챠 검증 실패 처리
                bindingResult.reject("recaptcha.error", "체크표시를 해주세요");
                return "board/review/board_form";
            }
        } catch (IOException e) {
            // IOException 처리
            e.printStackTrace();
            bindingResult.reject("recaptcha.error", "Error while verifying reCAPTCHA");
            return "board/review/board_form";
        }

        if (bindingResult.hasErrors()) {
            return "board/review/board_form";
        }

        ReviewBoard reviewBoard = this.reviewBoardService.getReviewBoard(id);
        if (!reviewBoard.getAuthor().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        List<String> profanityList = ProfanityListLoader.loadProfanityList("unsafe.txt");
        for (String profanity : profanityList) {
            if (reviewBoardForm.getContent().contains(profanity) || reviewBoardForm.getSubject().contains(profanity)) {
                model.addAttribute("error", "질문에 욕설이 포함되어 있습니다. 다시 작성해주세요.");
                return "board/review/board_form";
            }
        }

        try {
            this.reviewBoardService.modify(reviewBoard,reviewBoardForm.getSubject(), reviewBoardForm.getContent(), reviewImgFileList);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "질문 수정 중 오류가 발생하였습니다.");
            return "board/review/board_form";
        }

        return String.format("redirect:/review/board/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String reviewDelete(@AuthenticationPrincipal PrincipalDetails member,
                                 @PathVariable("id") Long id) {
        ReviewBoard reviewBoard = this.reviewBoardService.getReviewBoard(id);
        if (!reviewBoard.getAuthor().getName().equals(member.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        try {
            this.reviewBoardService.delete(reviewBoard);
        } catch (Exception e) {
            e.printStackTrace();
            // 삭제 중 에러 발생 시 에러 페이지로 리다이렉트 또는 다른 처리 방법 구현
            return "redirect:/error";
        }

        return "redirect:/review/board/list";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String reviewVote(@AuthenticationPrincipal PrincipalDetails member,
                             @PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        if (member == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요한 서비스입니다.");
            return "member/loginForm";
        }

        try {
            ReviewBoard reviewBoard = this.reviewBoardService.getReviewBoard(id);
            Member siteUser = this.memberService.getUser(member.getName());
            if (reviewBoard.getVoter().contains(siteUser)) {
                // 이미 투표한 경우에는 투표 취소
                this.reviewBoardService.cancelVote(reviewBoard, siteUser);
                redirectAttributes.addFlashAttribute("success", "추천이 취소되었습니다.");
            } else {
                this.reviewBoardService.vote(reviewBoard, siteUser);
                redirectAttributes.addFlashAttribute("success", "추천가 완료되었습니다.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "오류가 발생했습니다. 다시 시도해주세요.");
        }

        return String.format("redirect:/review/board/detail/%s", id);
    }





}
