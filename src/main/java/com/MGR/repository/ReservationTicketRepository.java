package com.MGR.repository;

import com.MGR.dto.ReservationDtlDto;
import com.MGR.entity.Reservation;
import com.MGR.entity.ReservationTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationTicketRepository extends JpaRepository<ReservationTicket, Long> {
    // 예약 내역에 들어가는 티켓을 저장하거나 조회
    List<ReservationTicket> findByReservationIdAndTicketId(Long reservationId, Long ticketId);
    ReservationTicket findByTicketId(Long ticketId);
    List<ReservationTicket> findAllByTicketId(Long ticketId);

    @Query("select rt from ReservationTicket rt " +
            "where rt.reservation.id = :reservationId " +
            "and (rt.reservationStatus = 'RESERVE' or rt.reservationStatus = 'CANCEL') " +
            "order by rt.reservationStatus desc, rt.reservationDate desc")
    Page<ReservationTicket> findByReservationId(@Param("reservationId") Long reservationId, Pageable pageable);

    @Query("select count(rt) from ReservationTicket rt " +
            "where rt.reservation.id = :reservationId " +
            "and (rt.reservationStatus = 'RESERVE' or rt.reservationStatus = 'CANCEL')")
    Long countReservation(@Param("reservationId") Long reservationId);

}