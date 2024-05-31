package com.MGR.entity;

import com.MGR.constant.CouponType;
import com.MGR.constant.DiscountType;
import com.MGR.dto.CouponFormDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter @Setter
@Entity
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 이름
    private Integer discountAmount; //할인 금액
    private Integer discountRate; //할인률
    private LocalDate startDate; // 사용 시작일
    private LocalDate endDate; // 사용 기한
    private String couponContent; // 쿠폰 내용

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public static Coupon createCoupon(String name, DiscountType discountType, LocalDate startDate, LocalDate endDate, String couponContent, CouponType couponType, Integer discountRate, Integer discountAmount) {
        Coupon coupon = new Coupon();

        coupon.setName(name);
        coupon.setDiscountType(discountType);
        coupon.setStartDate(startDate);
        coupon.setEndDate(endDate);
        coupon.setCouponContent(couponContent);
        coupon.setCouponType(couponType); // 쿠폰 타입 설정
        coupon.setDiscountRate(discountRate);
        coupon.setDiscountAmount(discountAmount);

        return coupon;
    }

    public void updateCoupon(CouponFormDto couponFormDto) {
        this.name = couponFormDto.getName();
        this.startDate = couponFormDto.getStartDate();
        this.endDate = couponFormDto.getEndDate();
        this.couponContent = couponFormDto.getCouponContent();
        this.couponType = couponFormDto.getCouponType();
        this.discountType = couponFormDto.getDiscountType();
        this.discountRate = couponFormDto.getDiscountRate(); //할인율 설정
        this.discountAmount = couponFormDto.getDiscountAmount(); //할인율 설정
    }
}
