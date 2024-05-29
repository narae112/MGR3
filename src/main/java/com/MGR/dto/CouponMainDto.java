package com.MGR.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class CouponMainDto {
    private Long id;
    private String name;
    private int discountRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String couponContent;
    private String imgUrl;

    @QueryProjection
    public CouponMainDto(Long id, String name, int discountRate, String imgUrl,
                         LocalDate startDate, LocalDate endDate, String couponContent) {
        this.id = id;
        this.name = name;
        this.discountRate = discountRate;
        this.imgUrl = imgUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.couponContent = couponContent;

    }
}
