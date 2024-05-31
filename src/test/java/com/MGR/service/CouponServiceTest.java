package com.MGR.service;

import com.MGR.constant.CouponType;
import com.MGR.entity.Coupon;
import com.MGR.entity.Member;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@SpringBootTest
class CouponServiceTest {
    @Mock
    private MemberService memberService;

    @InjectMocks
    private CouponService couponService;

    @Test
    public void testFindMembersWithBirthdayToday() {
        // Given
        Member memberWithBirthday = new Member();
        memberWithBirthday.setId(1L);
        memberWithBirthday.setName("John");
        memberWithBirthday.setBirth("2000-05-31"); // Assuming John's birthday is today

        Member memberWithoutBirthday = new Member();
        memberWithoutBirthday.setId(2L);
        memberWithoutBirthday.setName("Alice");
        memberWithoutBirthday.setBirth("1995-01-01");

        when(memberService.findMembersWithBirthdayToday()).thenReturn(Collections.singletonList(memberWithBirthday));
        when(memberService.findByAllUser()).thenReturn(Collections.singletonList(memberWithoutBirthday));

        Coupon birthdayCoupon = new Coupon();
        birthdayCoupon.setCouponType(CouponType.BIRTH);

        Coupon generalCoupon = new Coupon();
        generalCoupon.setCouponType(CouponType.ALL);

        // When
        List<Member> membersWithBirthday = memberService.findMembersWithBirthdayToday();
        List<Member> allMembers = memberService.findByAllUser();

        // Then
        assertTrue(membersWithBirthday.contains(memberWithBirthday));
        assertFalse(membersWithBirthday.contains(memberWithoutBirthday));

        assertFalse(allMembers.contains(memberWithBirthday));
        assertTrue(allMembers.contains(memberWithoutBirthday));
    }
}