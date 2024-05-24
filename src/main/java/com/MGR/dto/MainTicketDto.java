package com.MGR.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MainTicketDto {
    private Long id;
    private String name;
    private String memo;
    private String imgUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer price;

   @QueryProjection
    public MainTicketDto(Long id, String name, String memo,
                       String imgUrl, Integer price, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.memo = memo;
        this.imgUrl = imgUrl;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
    }


}
