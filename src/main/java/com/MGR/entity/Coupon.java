package com.MGR.entity;

import com.MGR.constant.CouponType;
import com.MGR.constant.LocationCategory;
import com.MGR.dto.CouponFormDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
@Entity
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 이름
    private int discountRate; // 할인률
    private LocalDate startDate; // 사용 시작일
    private LocalDate endDate; // 사용 기한
    private String couponContent; // 쿠폰 내용

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public static Coupon createCoupon(String name, int discountRate, LocalDate startDate, LocalDate endDate, String couponContent) {
        Coupon coupon = new Coupon();
        coupon.setName(name);
        coupon.setDiscountRate(discountRate);
        coupon.setStartDate(startDate);
        coupon.setEndDate(endDate);
        coupon.setCouponContent(couponContent);
        return coupon;
    }

    public void updateCoupon(CouponFormDto couponFormDto) {
        this.name = couponFormDto.getName();
        this.discountRate = couponFormDto.getDiscountRate();
        this.startDate = couponFormDto.getStartDate();
        this.endDate = couponFormDto.getEndDate();
        this.couponContent = couponFormDto.getCouponContent();
    }

}