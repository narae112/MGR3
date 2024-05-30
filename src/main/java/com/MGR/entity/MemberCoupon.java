package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@Entity
public class MemberCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column
    private String couponCode;
    // 쿠폰 코드
    private boolean used; // 사용 여부

    public static MemberCoupon memberGetCoupon(Member member, Coupon coupon){
        MemberCoupon memberCoupon = new MemberCoupon();
        memberCoupon.setMember(member);
        memberCoupon.setCoupon(coupon);
        memberCoupon.setCouponCode(generateCouponCode());
        memberCoupon.setUsed(false); // 쿠폰을 사용하지 않은 상태로 초기화

        return memberCoupon;
    }

    private static String generateCouponCode() {
        // UUID를 사용하여 무작위 쿠폰 코드 생성
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase();
    }
}
