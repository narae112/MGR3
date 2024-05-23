package com.MGR.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ReservationDtlDto {
    private Long reservationTicketId; // 예약 내역 티켓 아이디
    private String ticketNm; // 티켓 이름
    private int price; // 금액
    private int ticketCount; // 수량
    private LocalDateTime visitDate; // 방문예정일

    public ReservationDtlDto(Long reservationTicketId, String ticketNm, int price, int ticketCount, LocalDateTime visitDate) {
        this.reservationTicketId = reservationTicketId;
        this.ticketNm = ticketNm;
        this.price = price;
        this.ticketCount = ticketCount;
        this.visitDate = visitDate;
    }
}
