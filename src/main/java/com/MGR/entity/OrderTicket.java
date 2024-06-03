package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
@Table(name = "order_ticket")
public class OrderTicket {
    @Id
    @GeneratedValue
    @Column(name="order_ticket_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="ticket_id")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "reservation_ticket_id")
    private ReservationTicket reservationTicket;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    private int orderPrice; //주문가격
    private int count; //주문수량
    private LocalDate visitDate;

    public static OrderTicket createOrderTicket(Ticket ticket, int count, LocalDate visitDate) {
        OrderTicket orderTicket = new OrderTicket();
        orderTicket.setTicket(ticket);
        orderTicket.setCount(count);
//        orderTicket.setOrderPrice(ticket.getPrice());
        orderTicket.setVisitDate(visitDate);

        return orderTicket;
    }
    // 주문할 상품과 주문 수량을 통해 orderTicket 객체를 만드는 메서드 작성

    public int getTotalPrice() {
        return orderPrice * count;
    } // 물품 1개 항목 당 토탈가격(물건 갯수 * 물건 가격)

}