package com.MGR.dto;

import com.MGR.constant.LocationCategory;
import com.MGR.entity.OrderTicket;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class OrderTicketDto {
    private Long orderId;
    private LocationCategory locationCategory; // 지점
    private String name; // 티켓 이름
    private LocalDate visitDate; // 방문예정일
    private Integer adultCount; // 성인 인원
    private Integer childCount; // 아동 인원
    private int adultPrice; // 성인 금액
    private int childPrice; // 아동 금액
    private int adultTotalPrice; // 성인 전체 금액
    private int childTotalPrice; // 아동 전체 금액
    private int totalPrice; // 전체(성인 + 아동) 주문 금액
    private int totalCount;


    // 생성 메서드
    public OrderTicketDto(OrderTicket orderTicket) {
        this.orderId = orderTicket.getOrder().getId();
        this.locationCategory = orderTicket.getTicket().getLocationCategory();
        this.name = orderTicket.getTicket().getName();
        this.visitDate = orderTicket.getVisitDate();
        this.adultCount = orderTicket.getAdultCount();
        this.childCount = orderTicket.getChildCount();
        this.adultPrice = orderTicket.getTicket().getAdultPrice();
        this.childPrice = orderTicket.getTicket().getChildPrice();
        this.adultTotalPrice = orderTicket.getAdultTotalPrice();
        this.childTotalPrice = orderTicket.getChildTotalPrice();
        this.totalPrice = orderTicket.getTotalPrice();
        this.totalCount = orderTicket.getTotalCount();
    }

}