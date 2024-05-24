package com.MGR.repository;

import com.MGR.entity.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Reservation findByMemberId(Long memberId);
    // 현재 로그인 한 회원의 reservation 엔티티를 찾기 위해

}
