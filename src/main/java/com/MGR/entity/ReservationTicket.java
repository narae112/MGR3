package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "reservation_date")
    private LocalDateTime reservationDate; // 예약일

//    @Column(name = "visit_date")
//    private String visitDate; // 방문예정일

    @Column(name = "ticket_count")
    private int ticketCount; // 인원수

    public static ReservationTicket createReservationTicket(Reservation reservation,
                                                            Ticket ticket, int ticketCount){
        ReservationTicket reservationTicket = new ReservationTicket();
        reservationTicket.setReservation(reservation);
        reservationTicket.setTicket(ticket);
        reservationTicket.setTicketCount(ticketCount);
        reservationTicket.setReservationDate(LocalDateTime.now());

        return reservationTicket;
    }

    public void addCount(int ticketCount) {
        this.ticketCount += ticketCount;
    }

    public void updateCount(int ticketCount) {
        this.ticketCount = ticketCount;
    }

}
