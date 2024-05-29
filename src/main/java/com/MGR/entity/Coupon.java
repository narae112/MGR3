package com.MGR.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String couponCode; // 쿠폰 코드
    private double discountRate;
    private int maxUses;
    private LocalDate expirationDate; // 사용 기한

    public static Coupon createCoupon(String name, double discountRate, int maxUses, LocalDate expirationDate) {
        Coupon coupon = new Coupon();
        coupon.setName(name);
        coupon.setCouponCode(generateCouponCode());
        coupon.setDiscountRate(discountRate);
        coupon.setMaxUses(maxUses);
        coupon.setExpirationDate(expirationDate);
        return coupon;
    }

    private static String generateCouponCode() {
        // UUID를 사용하여 무작위 쿠폰 코드 생성
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase();
    }
}