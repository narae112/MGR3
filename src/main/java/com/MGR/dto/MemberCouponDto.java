package com.MGR.dto;

import com.MGR.entity.MemberCoupon;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class MemberCouponDto {
    private String name; // 쿠폰 이름
    private Integer discountAmount; //할인 금액
    private Integer discountRate; //할인률
    private LocalDate startDate; // 사용 시작일
    private LocalDate endDate; // 사용 기한
    private boolean used; // 사용 여부

    public MemberCouponDto(MemberCoupon memberCoupon) {
        this.name = memberCoupon.getCoupon().getName();
        this.discountAmount = memberCoupon.getCoupon().getDiscountAmount();
        this.discountRate = memberCoupon.getCoupon().getDiscountRate();
        this.startDate = memberCoupon.getCoupon().getStartDate();
        this.endDate = memberCoupon.getCoupon().getEndDate();
        this.used = memberCoupon.isUsed();
    }
}
