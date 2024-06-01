package com.MGR.controller;

import com.MGR.dto.CouponFormDto;
import com.MGR.dto.CouponMainDto;
import com.MGR.dto.CouponSearchDto;
import com.MGR.entity.Coupon;
import com.MGR.exception.DuplicateCouponNameException;
import com.MGR.service.CouponService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    // 쿠폰 등록 페이지
    @GetMapping(value = "/admin/coupon/new")
    public String couponForm(Model model) {
        model.addAttribute("couponFormDto", new CouponFormDto());
        return "coupon/couponForm";
    }

    // 쿠폰 등록
    @PostMapping("/admin/coupon/new")
    public String newCoupon(@Valid CouponFormDto couponFormDto, BindingResult bindingResult,
                            Model model, @RequestParam("couponImgFile") List<MultipartFile> couponImgFileList) {

        if (bindingResult.hasErrors()) {
            return "coupon/couponForm";
        }
        if (couponFormDto.getDiscountAmount() == null && couponFormDto.getDiscountRate() == null) {
            model.addAttribute("errorMessage", "할인액 또는 할인률을 입력해주세요");
            return "coupon/couponForm";
        }
        try {
            couponService.createCoupon(couponFormDto, couponImgFileList);
        } catch (DuplicateCouponNameException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "coupon/couponForm";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "쿠폰 등록 중 에러가 발생하였습니다");
            return "redirect:/admin/coupon/new"; // 에러 발생 시 폼으로 리다이렉트
        }
        return "redirect:/admin/coupons";// 성공 시 쿠폰 관리 페이지로 리다이렉트
    }

    //쿠폰 수정
    @GetMapping(value = "/admin/coupon/{couponId}")
    public String couponDtl(@PathVariable("couponId") Long couponId, Model model, RedirectAttributes redirectAttributes) {
        try {
            CouponFormDto couponFormDto = couponService.getCouponDtl(couponId);
            model.addAttribute("couponFormDto", couponFormDto);
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 티켓입니다.");
            model.addAttribute("couponFormDto", new CouponFormDto());
            return "coupon/couponForm";
        }
        return "coupon/couponForm";
    }

//    // 쿠폰 수정
//    @PostMapping(value = "/admin/coupon/{couponId}")
//    public String couponUpdate(@Valid CouponFormDto couponFormDto, BindingResult bindingResult,
//                               Model model, @RequestParam("couponImgFile") List<MultipartFile> couponImgFileList
//    ) {
//        if(couponImgFileList.get(0).isEmpty() && couponFormDto.getId() == null) {
//            model.addAttribute("errorMessage", "이미지는 필수 입력값입니다");
//            return "coupon/couponForm";
//        }
//        if (couponFormDto.getStartDate() == null || couponFormDto.getEndDate() == null) {
//            model.addAttribute("errorMessage", "날짜는 필수 입력값입니다");
//            return "coupon/couponForm";
//        }
//
//        try {
//            couponService.updateCoupon(couponFormDto, couponImgFileList);
//        } catch (Exception e) {
//            model.addAttribute("errorMessage", "쿠폰 수정 중 오류가 발생했습니다");
//            return "coupon/couponForm";
//        }
//
//        return "redirect:/admin/coupons"; // 성공 시 쿠폰 관리 페이지로 리다이렉트
//    }

    // 쿠폰 받기 페이지(예정)
    @GetMapping(value={"coupons", "/coupons/{page}"})
    public String couponMain(CouponSearchDto couponSearchDto,
                             @PathVariable Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.orElse(0), 6); // 페이지 번호
        Page<CouponMainDto> coupons = couponService.getCouponMainPage(couponSearchDto, pageable);

        model.addAttribute("coupons", coupons);
        model.addAttribute("couponSearchDto", couponSearchDto);
        model.addAttribute("maxPage", 5);

        return "coupon/admin/couponMain";
    }

    // 쿠폰 관리 페이지
    @GetMapping(value = {"/admin/coupons", "/admin/coupons/{page}"})
    public String couponManage(CouponSearchDto couponSearchDto,
                               @PathVariable("page") Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get():0, 3);
        Page<Coupon> coupons = couponService.getAdminCouponPage(couponSearchDto, pageable);
        model.addAttribute("coupons",coupons);
        model.addAttribute("couponSearchDto", couponSearchDto);
        model.addAttribute("maxPage",5);
        return "coupon/couponMng";
    }

//    // 쿠폰 삭제
//    @GetMapping("/admin/coupon/delete/{couponId}")
//    public String deleteCoupon(@PathVariable("couponId") Long couponId, RedirectAttributes redirectAttributes) {
//        try {
//            couponService.deleteCoupon(couponId);
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("errorMessage", "배포된 쿠폰은 삭제할 수 없습니다");
//            return "redirect:/admin/coupons";
//        }
//
//        return "redirect:/admin/coupons";
//    }

}
