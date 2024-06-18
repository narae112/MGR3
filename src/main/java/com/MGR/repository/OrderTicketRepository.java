package com.MGR.repository;

import com.MGR.entity.OrderTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderTicketRepository extends JpaRepository<OrderTicket, Long> {
    List<OrderTicket> findAllByTicketId(Long ticketId);

    List<OrderTicket> findByOrderId(Long id);
}