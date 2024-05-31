package com.MGR.controller;

import com.MGR.dto.AttractionDto;
import com.MGR.entity.Attraction;
import com.MGR.entity.Member;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.AttractionService;
import com.MGR.service.ImageService;
import com.MGR.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/attraction")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionService attractionService;
    private final MemberService memberService;
    private final ImageService imageService;

    @GetMapping("/attractionList")
    public String attractionList(Model model){

        List<Attraction> attractionList = attractionService.findAll();
        model.addAttribute("attractionList",attractionList);

        return "/attraction/attractionList";
    }

    @PostMapping("/create")
    public String createAttraction(@Valid AttractionDto attractionDto,
                                   BindingResult result, MultipartFile imgFile, Model model,
                                   @AuthenticationPrincipal PrincipalDetails member){

        Member findMember = memberService.findById(member.getId()).orElseThrow(
                ()->new NoSuchElementException("오류확인=> attraction controller error"));
        //인증된 사용자 확인

        if(!findMember.getRole().equals("ROLE_ADMIN")){
            //어드민계정 아니면 메인화면 이동
            return "redirect:/";
        }

        if(result.hasErrors()) {
            return "/attraction/attractionList";
        }

        try {
            attractionService.create(attractionDto,imgFile);
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage","어트랙션 등록 중 오류가 발생했습니다");
            return "/attraction/attractionList";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/";
    }



}
