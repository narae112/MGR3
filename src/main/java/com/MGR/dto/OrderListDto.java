package com.MGR.dto;

import com.MGR.constant.LocationCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class OrderListDto {
    private Long orderId;
    private LocalDate orderDate;
    private String orderNum;
    private String ticketName;
    private LocationCategory locationCategory;
    private Integer adultCount;
    private Integer childCount;
    private Integer adultPrice;
    private Integer childPrice;
    private Integer adultTotal; // 성인 결제 금액(count*price)
    private Integer childTotal;
    private Integer allTotalPrice; // 전체 결제 금액

}
