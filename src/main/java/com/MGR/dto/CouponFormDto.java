package com.MGR.dto;

import com.MGR.entity.Coupon;
import com.MGR.entity.Ticket;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class CouponFormDto {
    private Long id; // 쿠폰 아이디
    private String name; // 쿠폰 이름
    private int discountRate; // 할인률
    private LocalDate startDate; // 사용 시작일
    private LocalDate endDate; // 사용 기한
    private String couponContent; // 쿠폰 내용

    private List<ImageDto> couponImgDtoList = new ArrayList<>();

    private List<Long> couponImgIds = new ArrayList<>();
}
