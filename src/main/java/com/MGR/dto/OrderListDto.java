package com.MGR.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class OrderListDto {
    private Long orderId;
    private LocalDate orderDate;
    private String orderNum;

}
