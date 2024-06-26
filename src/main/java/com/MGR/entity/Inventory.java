package com.MGR.entity;

import com.MGR.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    private int quantity; // 재고 수량
    private LocalDate date; // 날짜

    public static Inventory createInventory(Ticket ticket, int quantity, LocalDate date) {
        Inventory inventory = new Inventory();
        inventory.setTicket(ticket);
        inventory.setQuantity(quantity);
        inventory.setDate(date);
        return inventory;
    }

//    void init(){
//        Ticket ticket = new Ticket();
//        ticket.setId(1L);
//        ticket.setName("test");
//        ticket.setPrice(15000);
//        Inventory inventory = new Inventory();
//        inventory.setTicket(ticket);
//        inventory.setQuantity(100);
//        inventory.setDate(LocalDate.now());
//    }

    public void removeQuantity(int reserveCount) {
        int rest = this.quantity - reserveCount; // 남은 재고 = 현재 재고 - 넘어온 주문수량
        if(rest < 0) {
            throw new OutOfStockException("티켓의 재고가 부족합니다 [현재 재고 수량 : " + this.quantity + " 개]");
        }
        // 남은 재고가 0개 이하가 되면 예외 발생
        this.quantity = rest;
        // 재고가 부족하지 않으면 주문 수량을 제외한 남은 재고를 현재 재고로
    }

    public void addQuantity(int ticketCount) {
        this.quantity += ticketCount;
    }

}