//package com.MGR.entity;
//
//import com.MGR.constant.ReservationStatus;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Getter @Setter
//public class Reservation { // = 예약 List
//    // 회원 한 명 당 하나의 예약 List 를 갖는다
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "reservation_id")
//    private Long id;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    private Member member;
//
//    @OneToMany
//    @JoinColumn(name = "ticket_id")
//    private List<Ticket> ticketList;
//    @OneToMany(mappedBy = "reservation" ,cascade=CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<ReservationTicket> reservationTickets = new ArrayList<>();
//
//    @Enumerated(EnumType.STRING)
//    private ReservationStatus reservationStatus;
//
//    public static Reservation createReservation(Member member) {
//        Reservation reservation = new Reservation();
//        reservation.setMember(member);
//        return reservation;
//    }
//
//    public void cancelReservation() {
//        this.reservationStatus = ReservationStatus.CANCEL;
//    }
//
//}
