package com.MGR.controller;

import com.MGR.dto.AttractionDto;
import com.MGR.entity.Attraction;
import com.MGR.entity.EventBoard;
import com.MGR.entity.Image;
import com.MGR.entity.Member;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.AttractionService;
import com.MGR.service.ImageService;
import com.MGR.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/attraction")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionService attractionService;
    private final MemberService memberService;
    private final ImageService imageService;

    @GetMapping({"/attractionList", "/attractionList/{page}"})
    public String attractionList(Model model, @RequestParam(value = "page", defaultValue = "0") int page){
        Page<Attraction> paging = attractionService.getAttractionList(page);
        model.addAttribute("paging", paging);

        Map<Long,String> imageUrls  = new HashMap<>();
        for (Attraction attraction : paging) {
            Image image = imageService.findByAttraction(attraction);
            imageUrls.put(attraction.getId(),image.getImgUrl());
        }
        model.addAttribute("imageUrls", imageUrls);

        return "/attraction/attractionList";
    }

    @GetMapping("/new")
    public String attractionForm(Model model){
        //게시글 입력 폼
        model.addAttribute("attractionDto",new Attraction());

        return "/attraction/attractionForm";
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
            Attraction attraction = attractionService.saveAttraction(attractionDto, imgFile);
            return "redirect:/attraction/" + attraction.getId();
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage","어트랙션 등록 중 오류가 발생했습니다");
            return "/attraction/attractionList";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{id}")
    public String attractionDtl(@PathVariable("id") Long id, Model model){

        Attraction attraction = attractionService.findById(id).orElseThrow();
        model.addAttribute("attraction",attraction);

        Image attractionImage = imageService.findByAttraction(attraction);
        model.addAttribute("attractionImage",attractionImage);

        return "attraction/attractionDtl";
    }

    @PostMapping("/delete/{id}") //게시글 삭제
    @ResponseBody
    public String attractionDelete(@PathVariable("id") Long id){

        Optional<Attraction> attraction = attractionService.findById(id);
        if(attraction.isPresent()){
            attractionService.delete(attraction.get());
            return "놀이기구가 삭제되었습니다";
        }
        return "삭제 오류";
    }

    @GetMapping("/edit/{id}")
    public String editAttraction(@PathVariable("id") Long id, Model model,
                                 @AuthenticationPrincipal PrincipalDetails member){

        Attraction attraction = attractionService.findById(id).orElseThrow();
        model.addAttribute("attractionDto",attraction);

        Image image = imageService.findByAttraction(attraction);
        model.addAttribute("attractionImg",image);

        return "attraction/attractionForm";
    }

    @PostMapping("/update/{id}")
    public String updateAttraction(@PathVariable("id") Long id,
                                   @Valid AttractionDto attractionDto,
                                   BindingResult result, Model model,
                                   @RequestParam(value = "imgFile", required = false) MultipartFile imgFile) {
        if (result.hasErrors()) {
            model.addAttribute("attractionDto", attractionDto);
            return "attraction/attractionForm";
        }

        try {
            attractionService.update(id, attractionDto, imgFile);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "수정 중 오류가 발생했습니다.");
            return "attraction/attractionForm";
        }

        return "redirect:/attraction/" + id;
    }

    @GetMapping({"/schedule", "/schedule/{date}"})
    public String attractionSchedule(@PathVariable(value = "date", required = false) String dateStr,
                                     Model model) {
        System.out.println("dateStr = " + dateStr);
        LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();
        System.out.println("date = " + date);
        int dayOfMonth = date.getDayOfMonth();
        System.out.println("dayOfMonth = " + dayOfMonth);
        List<Attraction> attractions = attractionService.findAll();
        //운휴가 오늘인 놀이기구 필터링
        List<Attraction> attractionList = attractions.stream()
                .filter(attraction -> attraction.getClosureDay() == dayOfMonth)
                .collect(Collectors.toList());
        model.addAttribute("attractionList", attractionList);

        //이미지 찾기
        Map<Long,String> imageUrls  = new HashMap<>();
        for (Attraction attraction : attractionList) {
            Image image = imageService.findByAttraction(attraction);
            imageUrls.put(attraction.getId(),image.getImgUrl());
        }
        model.addAttribute("imageUrls", imageUrls);
        model.addAttribute("schedule", date);

        return "attraction/schedule";
    }
}
