package com.MGR.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainTicketDto {
    private Long id;
    private String name;
    private String memo;
    private String imgUrl;

    private Integer price;

   @QueryProjection
    public MainTicketDto(Long id, String name, String memo,
                       String imgUrl, Integer price) {
        this.id = id;
        this.name = name;
        this.memo = memo;
        this.imgUrl = imgUrl;
        this.price = price;
    }
}
