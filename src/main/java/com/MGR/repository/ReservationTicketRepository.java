package com.MGR.repository;

import com.MGR.dto.ReservationDtlDto;
import com.MGR.entity.Reservation;
import com.MGR.entity.ReservationTicket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationTicketRepository extends JpaRepository<ReservationTicket, Long> {
    // 예약 내역에 들어가는 티켓을 저장하거나 조회
    List<ReservationTicket> findByReservationIdAndTicketId(Long reservationId, Long ticketId);

    @Query("select rt from ReservationTicket rt " +
            "where rt.reservation.id = :reservationId " +
            "order by rt.reservationDate desc"
    )
    List<ReservationDtlDto> findReservations(@Param("reservationId")Long id, Pageable pageable);

    @Query("select count(rt) from ReservationTicket rt " +
            "where rt.reservation.id = :reservationId "
    )
    Long countReservation(@Param("reservationId")Long id);


}
