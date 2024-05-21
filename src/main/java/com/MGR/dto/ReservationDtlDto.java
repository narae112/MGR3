package com.MGR.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReservationDtlDto {
    private Long reservationTicketId; // 예약 내역 티켓 아이디
    private String ticketNm; // 티켓 이름
    private int price; // 금액
    private int ticketCount; // 수량

    public ReservationDtlDto(Long reservationTicketId, String ticketNm, int price, int ticketCount) {
        this.reservationTicketId = reservationTicketId;
        this.ticketNm = ticketNm;
        this.price = price;
        this.ticketCount = ticketCount;
    }
}
