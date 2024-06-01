package com.MGR.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.MGR.dto.QCouponMainDto is a Querydsl Projection type for CouponMainDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QCouponMainDto extends ConstructorExpression<CouponMainDto> {

    private static final long serialVersionUID = -708449970L;

    public QCouponMainDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<Integer> discountRate, com.querydsl.core.types.Expression<String> imgUrl, com.querydsl.core.types.Expression<java.time.LocalDate> startDate, com.querydsl.core.types.Expression<java.time.LocalDate> endDate, com.querydsl.core.types.Expression<String> couponContent) {
        super(CouponMainDto.class, new Class<?>[]{long.class, String.class, int.class, String.class, java.time.LocalDate.class, java.time.LocalDate.class, String.class}, id, name, discountRate, imgUrl, startDate, endDate, couponContent);
    }

}

