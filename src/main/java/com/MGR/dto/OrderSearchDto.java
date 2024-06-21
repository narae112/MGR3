package com.MGR.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrderSearchDto {
    private String searchDateType;
    private LocalDate startDate; // New field for start date
    private LocalDate endDate;

}
