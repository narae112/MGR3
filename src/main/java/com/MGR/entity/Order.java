package com.MGR.entity;

import com.MGR.constant.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter @ToString
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    private String orderNum; // 주문번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate; // 주문일

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus; // 결제상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderTicket> orderTickets = new ArrayList<>();
    //cascade=CascadeType.ALL 모든 변경 유형이 자식에 영향(저장,업데이트,삭제)
    //orphanRemoval = true 부모엔티티에서 자식 엔티티를 제거할 때 자식엔티티도 자동삭제

    public void addOrderTicket(OrderTicket orderTicket) {
        orderTickets.add(orderTicket); // 주문항목을 주문에 추가
        orderTicket.setOrder(this); // 주문 항목의 주문을 현재 주문으로 설정
        // 양방향 편의 메서드
        // orderTicket 엔티티는 order 엔티티에 속하고 order 엔티티는 여러개의 orderTicket 엔티티를 가질 수 있다
        // 이 메서드는 order 엔티티에 새로운 orderTicket 을 추가하고 동시에 orderTicket 의 order 속성을 현재 주문으로 설정하며
        // 양방향 연관관계를 관리한다
    }

    // 주문
    public static Order createOder(Member member, List<OrderTicket> orderTicketList) {
        Order order = new Order();
        order.setMember(member);
        for (OrderTicket orderTicket : orderTicketList) {
            order.addOrderTicket(orderTicket);
        }
        order.setReservationStatus(ReservationStatus.RESERVE);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderNum(UUID.randomUUID().toString());
        return order;
    } // 주문 티켓 객체를 이용하여 주문 객체를 만드는 메서드 작성

    public int getAllTotalPrice() {
        int allTotalPrice = 0;
        for (OrderTicket orderTicket : orderTickets) {
            allTotalPrice += orderTicket.getTotalPrice();
        }
        return allTotalPrice;
    }

    public void cancelOrder() {
        this.reservationStatus = ReservationStatus.CANCEL;
    }
}