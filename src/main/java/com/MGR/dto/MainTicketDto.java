package com.MGR.dto;

import com.MGR.constant.LocationCategory;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MainTicketDto {
    private Long id;
    private String name;
    private String memo;
    private String imgUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocationCategory locationCategory;

   @QueryProjection
    public MainTicketDto(Long id, String name, String memo,
                       String imgUrl,  LocalDate startDate, LocalDate endDate, LocationCategory locationCategory) {
        this.id = id;
        this.name = name;
        this.memo = memo;
        this.imgUrl = imgUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.locationCategory = locationCategory;
    }


}
