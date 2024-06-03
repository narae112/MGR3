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

//    @ManyToOne
//    @JoinColumn(name = "reservation_ticket_id")
//    private ReservationTicket reservationTicket;

    @Column
    private Long reservationTicketId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    @Column
    private int adultPrice; // 성인 티켓 가격
    private int childPrice; // 아동 티켓 가격
    private int adultCount; // 성인 주문 수량
    private int childCount; // 아동 주문 수량
    private LocalDate visitDate;

    public static OrderTicket createOrderTicket(Ticket ticket, Long reservationTicketId, int adultCount, int childCount, LocalDate visitDate) {

        OrderTicket orderTicket = new OrderTicket();

        orderTicket.setTicket(ticket);
        orderTicket.setReservationTicketId(reservationTicketId);
        orderTicket.setAdultPrice(ticket.getAdultPrice);
        orderTicket.setChildCount(ticket.getChildPrice);
        orderTicket.setAdultCount(adultCount);
        orderTicket.setChildCount(childCount);
        orderTicket.setVisitDate(visitDate);

        return orderTicket;
    }
    // 주문할 상품과 주문 수량을 통해 orderTicket 객체를 만드는 메서드 작성

    public int getAdultTotalPrice() { return adultPrice * adultCount; } // 성인 티켓 토탈 가격(성인 티켓 가격 * 성인 인원수)
    public int getChildTotalPrice() {
        return childPrice * childCount;
    } // 아동 티켓 토탈 가격

}