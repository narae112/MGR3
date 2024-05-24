package com.MGR.repository;

import com.MGR.entity.EventBoard;
import com.MGR.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventBoardRepository extends JpaRepository<EventBoard, Long> {

    @Query("SELECT e FROM EventBoard e JOIN FETCH e.member WHERE e.id = :id")
    EventBoard findByIdWithMember(@Param("id") Long id);

    @Override
    Page<EventBoard> findAll(Pageable pageable);
}
