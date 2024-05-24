package com.MGR.dto;

import com.MGR.constant.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
public class ReservationDtlDto {
    private Long reservationTicketId; // 예약 내역 티켓 아이디
    private String name; // 티켓 이름
    private int price; // 금액
    private int ticketCount; // 수량
    private String visitDate; // 방문예정일
    private LocalDateTime reservationDate; // 예약일
    private ReservationStatus reservationStatus; // 예약 상태

    public ReservationDtlDto(Long reservationTicketId, String name,
                             int price, int ticketCount, String visitDate, LocalDateTime reservationDate, ReservationStatus reservationStatus) {
        this.reservationTicketId = reservationTicketId;
        this.name = name;
        this.price = price;
        this.ticketCount = ticketCount;
        this.visitDate = visitDate;
        this.reservationDate = reservationDate;
        this.reservationStatus = reservationStatus;
    }
}
