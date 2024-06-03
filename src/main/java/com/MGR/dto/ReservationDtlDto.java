package com.MGR.dto;

import com.MGR.constant.LocationCategory;
import com.MGR.constant.ReservationStatus;
import com.MGR.constant.TicketCategory;
import com.MGR.entity.ReservationTicket;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter @Setter
public class ReservationDtlDto {
    private Long reservationTicketId; // 예약 내역 티켓 아이디
    private String name; // 티켓 이름
    private int price; // 금액
    private int ticketCount; // 수량
    private LocationCategory locationCategory; // 지점
    private String visitDate; // 방문예정일
    private String reservationDate; // 예약일
    private ReservationStatus reservationStatus; // 예약 상태
    private TicketCategory ticketCategory; // 성인/아동

    public ReservationDtlDto(ReservationTicket reservationTicket) {
        this.reservationTicketId = reservationTicket.getId();
        this.name = reservationTicket.getTicket().getName();
//        this.price = reservationTicket.getTicket().getPrice();
        this.ticketCount = reservationTicket.getTicketCount();
        this.locationCategory = reservationTicket.getTicket().getLocationCategory();
        this.visitDate = reservationTicket.getVisitDate();
        this.reservationDate = reservationTicket.getReservationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.reservationStatus = reservationTicket.getReservationStatus();
//        this.ticketCategory = reservationTicket.getTicket().getTicketCategory();
    }

}