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

    public QMainTicketDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<String> memo, com.querydsl.core.types.Expression<String> imgUrl, com.querydsl.core.types.Expression<Integer> price, com.querydsl.core.types.Expression<java.time.LocalDateTime> startDate, com.querydsl.core.types.Expression<java.time.LocalDateTime> endDate) {
        super(MainTicketDto.class, new Class<?>[]{long.class, String.class, String.class, String.class, int.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class}, id, name, memo, imgUrl, price, startDate, endDate);
    }

}

