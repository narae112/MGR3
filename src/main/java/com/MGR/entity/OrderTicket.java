package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter @Setter @ToString
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

    @Column
    private int adultPrice; // 성인 티켓 가격
    private int childPrice; // 아동 티켓 가격
    private Integer adultCount; // 성인 주문 수량
    private Integer childCount;
    private LocalDate visitDate;

    //    public static OrderTicket createOrderTicket(Ticket ticket, ReservationTicket reservationTicket, int adultCount, int childCount, LocalDate visitDate) {
//
//        OrderTicket orderTicket = new OrderTicket();
//
//        orderTicket.setTicket(ticket);
//        orderTicket.setReservationTicket(reservationTicket);
//        orderTicket.setAdultPrice(ticket.getAdultPrice());
//        orderTicket.setChildCount(ticket.getChildPrice());
//        orderTicket.setAdultCount(adultCount);
//        orderTicket.setChildCount(childCount);
//        orderTicket.setVisitDate(visitDate);
//
//        return orderTicket;
//    } // 주문할 상품과 주문 수량을 통해 orderTicket 객체를 만드는 메서드 작성
//
//    public int getAdultTotalPrice() { return adultPrice * adultCount; } // 성인 티켓 토탈 가격(성인 티켓 가격 * 성인 인원수)
//    public int getChildTotalPrice() {
//        return childPrice * childCount;
//    } // 아동 티켓 토탈 가격
//
//    public int getTotalPrice() { return ((adultPrice * adultCount) + (childPrice * childCount)); } // 전체(성인 + 아동) 주문 가격
    public OrderTicket() {
    }

    // 생성자 추가
    public OrderTicket(Ticket ticket, ReservationTicket reservationTicket, int adultCount, int childCount, LocalDate visitDate) {
        this.ticket = ticket;
        this.reservationTicket = reservationTicket;
        this.adultPrice = ticket.getAdultPrice();
        this.childPrice = ticket.getChildPrice();
        this.adultCount = adultCount;
        this.childCount = childCount;
        this.visitDate = visitDate;
    }

    // 주문할 상품과 주문 수량을 통해 orderTicket 객체를 만드는 메서드 작성
    public static OrderTicket createOrderTicket(Ticket ticket, ReservationTicket reservationTicket, int adultCount, int childCount, LocalDate visitDate) {
        return new OrderTicket(ticket, reservationTicket, adultCount, childCount, visitDate);
    }

    // 성인 티켓 토탈 가격(성인 티켓 가격 * 성인 인원수)
    public int getAdultTotalPrice() {
        return adultPrice * adultCount;
    }

    // 아동 티켓 토탈 가격
    public int getChildTotalPrice() {
        return childPrice * childCount;
    }

    // 전체(성인 + 아동) 주문 가격
    public int getTotalPrice() {
        return getAdultTotalPrice() + getChildTotalPrice();
    }
}