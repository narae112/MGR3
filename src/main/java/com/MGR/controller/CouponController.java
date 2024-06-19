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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @PostMapping("/admin/coupon/new")
    public ResponseEntity<Map<String, String>> newCouponAjax(@Valid CouponFormDto couponFormDto, BindingResult bindingResult,
                                                             @RequestParam("couponImgFile") List<MultipartFile> couponImgFileList) {
        if (bindingResult.hasFieldErrors("name")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("field", "name");
            errorResponse.put("message", "쿠폰명을 입력해주세요");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (bindingResult.hasFieldErrors("discountRate")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("field", "discountRate");
            errorResponse.put("message", "할인율을 올바르게 입력해주세요");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (bindingResult.hasFieldErrors("couponContent")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("field", "couponContent");
            errorResponse.put("message", "쿠폰 세부사항을 입력해주세요");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (bindingResult.hasFieldErrors("startDate")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("field", "startDate");
            errorResponse.put("message", "쿠폰 시작 날짜를 입력해주세요");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (bindingResult.hasFieldErrors("endDate")) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("field", "endDate");
            errorResponse.put("message", "쿠폰 종료 날짜를 입력해주세요");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // 이미지 파일이 없는 경우
        if (couponImgFileList.isEmpty() || couponImgFileList.get(0).isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("field", "couponImgFile");
            errorResponse.put("message", "상품 이미지는 필수 입력 값입니다");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            couponService.createCoupon(couponFormDto, couponImgFileList);
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "쿠폰이 성공적으로 등록되었습니다");
            return ResponseEntity.ok().body(successResponse);
        } catch (DuplicateCouponNameException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "쿠폰 등록 중 오류가 발생했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    //쿠폰 수정
    @GetMapping(value = "/admin/coupon/{couponId}")
    public String couponDtl(@PathVariable("couponId") Long couponId, Model model, RedirectAttributes redirectAttributes) {
        try {
            CouponFormDto couponFormDto = couponService.getCouponDtl(couponId);
            model.addAttribute("couponFormDto", couponFormDto);
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 티켓입니다");
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

//     @GetMapping(value={"coupons", "/coupons/{page}"})
//    public String couponMain(CouponSearchDto couponSearchDto,
//                             @PathVariable Optional<Integer> page, Model model){
//        Pageable pageable = PageRequest.of(page.orElse(0), 6); // 페이지 번호
//        Page<CouponMainDto> coupons = couponService.getCouponMainPage(couponSearchDto, pageable);
//
//        model.addAttribute("coupons", coupons);
//        model.addAttribute("couponSearchDto", couponSearchDto);
//        model.addAttribute("maxPage", 5);
//
//        return "coupon/admin/couponMain";
//    }

    // 쿠폰 관리 페이지
    @GetMapping(value = {"/admin/coupons", "/admin/coupons/{page}"})
    public String couponManage(CouponSearchDto couponSearchDto,
                               @PathVariable("page") Optional<Integer> page, Model model){
        // 페이지 매개변수 처리
        int pageNumber = page.orElse(0); // 페이지 매개변수가 없는 경우 0으로 초기화
        Pageable pageable = PageRequest.of(pageNumber, 5);

        // 쿠폰 페이지 가져오기
        Page<Coupon> coupons = couponService.getAdminCouponPage(couponSearchDto, pageable);

        // 모델에 쿠폰 페이지 및 기타 데이터 추가
        model.addAttribute("coupons", coupons);
        model.addAttribute("couponSearchDto", couponSearchDto);
        model.addAttribute("pageNumber", coupons.getNumber()); // 페이지 번호 추가

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