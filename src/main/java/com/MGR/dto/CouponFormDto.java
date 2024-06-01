package com.MGR.dto;

import com.MGR.constant.CouponType;
import com.MGR.constant.DiscountType;
import com.MGR.entity.Coupon;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class CouponFormDto {
    private Long id; // 쿠폰 아이디

    @NotBlank(message = "쿠폰 이름을 입력하세요")
    private String name; // 쿠폰 이름

    @NotNull(message = "사용 시작일을 선택하세요")
    private LocalDate startDate; // 사용 시작일

    @NotNull(message = "사용 마감일을 선택하세요")
    private LocalDate endDate; // 사용 기한

    @NotBlank(message = "쿠폰 설명을 입력하세요")
    private String couponContent; // 쿠폰 내용

    private CouponType couponType;

    private DiscountType discountType;

    private Integer discountRate; // 할인율

    private Integer discountAmount; // 할인액

    private List<ImageDto> couponImgDtoList = new ArrayList<>();

    private List<Long> couponImgIds = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    public Coupon createCoupon() {
        return modelMapper.map(this, Coupon.class);
    }

    public static CouponFormDto of(Coupon coupon) {
        return modelMapper.map(coupon, CouponFormDto.class);
    }

    @AssertTrue(message = "할인율 또는 할인액을 올바르게 입력하세요")
    public boolean isValidDiscount() {
        if (discountType == DiscountType.PERCENT) {
            return discountRate != null && discountAmount == null;
        } else if (discountType == DiscountType.AMOUNT) {
            return discountAmount != null && discountRate == null;
        }
        return true;
    }
}
