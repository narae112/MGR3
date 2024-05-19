package com.MGR.dto;

import com.MGR.constant.TicketCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class TicketDto {

    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String name;
    private Integer price;
    private String memo;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;
}
