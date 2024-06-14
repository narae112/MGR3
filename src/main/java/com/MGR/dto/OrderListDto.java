package com.MGR.dto;

import com.MGR.constant.LocationCategory;
import com.MGR.entity.MemberCoupon;
import com.MGR.entity.Order;
import com.MGR.entity.OrderTicket;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class OrderListDto {
    private Long orderId;
    private String orderNum; // 주문 번호
    private LocalDateTime orderDate; // 주문일
    private int discountRate;
    private List<OrderTicketDto> orderTickets = new ArrayList<>();

    public void addOrderTicket(OrderTicketDto orderTicketDto){
        orderTickets.add(orderTicketDto);
    }

    public OrderListDto(Order order) {
        this.orderId = order.getId();
        this.orderNum = order.getOrderNum();
        this.orderDate = order.getOrderDate();
    }
}
