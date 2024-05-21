package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Reservation { // = 예약 List
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Reservation createReservation(Member member) {
        Reservation reservation = new Reservation();
        reservation.setMember(member);
        return reservation;
    }
    // 회원 한 명 당 하나의 예약 List 를 갖는다
}
