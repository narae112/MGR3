package com.MGR.entity;

import com.MGR.constant.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "reservation_ticket")
public class ReservationTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column // 인원수 = 티켓수
    private int adultCount; // 성인 인원수

    @Column
    private int childCount; // 아동 인원수

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus; // 예약 상태(예약 / 취소 / 결제완료)

    public static ReservationTicket createReservationTicket(Reservation reservation,
                                                            Ticket ticket, int adultCount, int childCount, String visitDate){
        ReservationTicket reservationTicket = new ReservationTicket();
        reservationTicket.setReservation(reservation);
        reservationTicket.setTicket(ticket);
        reservationTicket.setAdultCount(adultCount);
        reservationTicket.setChildCount(childCount);
        reservationTicket.setVisitDate(visitDate);
        reservationTicket.setReservationDate(LocalDateTime.now());
        reservationTicket.setReservationStatus(ReservationStatus.RESERVE);

        return reservationTicket;
    }

    public void addCount(int adultCount, int childCount) {
        this.adultCount += adultCount;
        this.childCount += childCount;
    }

    public int getAllCount() {
        return adultCount + childCount;
    }

    public void updateAdultCount(int adultCount) {
        this.adultCount = adultCount;
    }

    public void updateChildCount(int childCount) {
        this.childCount = childCount;
    }

    public void updateAllCount(int adultCount, int childCount) {
        this.adultCount = adultCount;
        this.childCount = childCount;
    }

    public void cancelReservation() {
        this.reservationStatus = ReservationStatus.CANCEL;
    }

}