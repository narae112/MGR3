package com.MGR.entity;

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
    private Reservation reservation;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ticket_id")
    private Ticket ticket;

    private LocalDateTime reservationDate; // 예약일
    private LocalDate visitDate; // 방문예정일
    private int ticketCount; // 인원수

    public static ReservationTicket createReservationTicket(Reservation reservation,
                                                            Ticket ticket, int ticketCount, LocalDate visitDate){
        ReservationTicket reservationTicket = new ReservationTicket();
        reservationTicket.setReservation(reservation);
        reservationTicket.setTicket(ticket);
        reservationTicket.setTicketCount(ticketCount);
        reservationTicket.setVisitDate(visitDate);

        return reservationTicket;
    }

    public void addCount(int ticketCount) {
        this.ticketCount += ticketCount;
    }

    public void updateCount(int ticketCount) {
        this.ticketCount = ticketCount;
    }

}
