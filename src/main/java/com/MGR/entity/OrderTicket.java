package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class OrderTicket {
    @Id
    @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) //지연로딩
    @JoinColumn(name="item_id")
    private Ticket ticket;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    private int orderPrice; //주문가격
    private int count; //주문수량

    public static OrderTicket createOrderTicket(Ticket ticket, int count) {
        OrderTicket orderTicket = new OrderTicket();
        orderTicket.setTicket(ticket);
        orderTicket.setCount(count);
        orderTicket.setOrderPrice(ticket.getPrice());

        return orderTicket;
    }
    // 주문할 상품과 주문 수량을 통해 orderTicket 객체를 만드는 메서드 작성

    public int getTotalPrice() {
        return orderPrice * count;
    } // 물품 1개 항목 당 토탈가격(물건 갯수 * 물건 가격)

}
