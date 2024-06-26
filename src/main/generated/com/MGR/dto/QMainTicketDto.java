package com.MGR.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.MGR.dto.QMainTicketDto is a Querydsl Projection type for MainTicketDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QMainTicketDto extends ConstructorExpression<MainTicketDto> {

    private static final long serialVersionUID = -907530136L;

    public QMainTicketDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<String> memo, com.querydsl.core.types.Expression<String> imgUrl, com.querydsl.core.types.Expression<java.time.LocalDate> startDate, com.querydsl.core.types.Expression<java.time.LocalDate> endDate, com.querydsl.core.types.Expression<com.MGR.constant.LocationCategory> locationCategory) {
        super(MainTicketDto.class, new Class<?>[]{long.class, String.class, String.class, String.class, java.time.LocalDate.class, java.time.LocalDate.class, com.MGR.constant.LocationCategory.class}, id, name, memo, imgUrl, startDate, endDate, locationCategory);
    }

}

