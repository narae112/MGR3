package com.MGR.dto;

import com.MGR.constant.LocationCategory;
import com.MGR.constant.TicketCategory;
import com.MGR.entity.Order;
import com.MGR.entity.OrderTicket;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class OrderTicketDto {
    private Long orderId;
    private LocationCategory locationCategory; // 지점
    private String name; // 티켓 이름
    private TicketCategory ticketCategory; // 연령구분(성인/아동)
    private LocalDate visitDate; // 방문예정일
    private int count; // 주문 수량
    private int orderPrice; // 주문 금액
    private int totalPrice; // 전체 주문 금액


    // 생성 메서드
    public OrderTicketDto(OrderTicket orderTicket) {
        this.orderId = orderTicket.getOrder().getId();
        this.locationCategory = orderTicket.getTicket().getLocationCategory();
        this.name = orderTicket.getTicket().getName();
//        this.ticketCategory = orderTicket.getTicket().getTicketCategory();
        this.visitDate = orderTicket.getVisitDate();
        this.count = orderTicket.getCount();
        this.orderPrice = orderTicket.getOrderPrice();
        this.totalPrice = orderTicket.getTotalPrice();
    }

}