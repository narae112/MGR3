package com.MGR.repository;

import com.MGR.dto.ReservationDtlDto;
import com.MGR.dto.ReservationTicketDto;
import com.MGR.entity.ReservationTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationTicketRepository extends JpaRepository<ReservationTicket, Long> {
    // 예약 내역에 들어가는 티켓을 저장하거나 조회
    ReservationTicket findByReservationIdAndTicketId(Long reservationId, Long ticketId);

    //jpql 에서는 new 연산자를 이용하여 결과를 객체로 직접 변환할 수 있음
    @Query("Select new com.MGR.dto.ReservationDtlDto(rt.id, t.name, t.price, rt.ticketCount, rt.visitDate) " +
            "from ReservationTicket rt " + // 조회 대상 테이블
            "join rt.ticket t " + // ReservationTicket 엔티티의 ticket 연관관계를 이용, ticket 엔티티를 t 라는 별칭으로 조인, 예약내역의 티켓과 연결된 정보를 가져옴
            "where rt.reservation.id = :reservationId " + // 연관관계를 이용, 예약 id 필터링 [reservationId 파라미터값과 일치하는 예약내역]에 속한 [예약 티켓]만 조회
            "order by rt.reservationDate desc" // 예약일 순으로 정렬
    )
    List<ReservationDtlDto> findReservationDtlDtoList(Long ReservationId);
    // 특정 예약 내역 id 에 해당하는 예약 내역 티켓 항목을 조회하고 각 항목의 티켓에 대한 정보를
    // ReservationDtlDto 객체에 담아 내림차순으로 정렬된 결과 반환

}
