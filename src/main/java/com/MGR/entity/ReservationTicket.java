package com.MGR.entity;

import com.MGR.constant.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class ReservationTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_ticket_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="reservation_id")
    private Reservation reservation; // member 한 명 당 하나의 reservation

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ticket_id")
    private Ticket ticket;

    @Column
    private LocalDateTime reservationDate; // 예약일

    @Column
    private String visitDate; // 방문예정일
    private int ticketCount; // 인원수


    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus; // 예약 상태(예약 / 취소 / 결제완료)

    public static ReservationTicket createReservationTicket(Reservation reservation,
                                                            Ticket ticket, int ticketCount, String visitDate){
        ReservationTicket reservationTicket = new ReservationTicket();
        reservationTicket.setReservation(reservation);
        reservationTicket.setTicket(ticket);
        reservationTicket.setTicketCount(ticketCount);
        reservationTicket.setVisitDate(visitDate);
        reservationTicket.setReservationDate(LocalDateTime.now());
        reservationTicket.setReservationStatus(ReservationStatus.RESERVE);

        return reservationTicket;
    }

    public void addCount(int ticketCount) {
        this.ticketCount += ticketCount;
    }

    public void updateCount(int ticketCount) {
        this.ticketCount = ticketCount;
    }

    public void cancelReservation() {
        this.reservationStatus = ReservationStatus.CANCEL;
    }

}
