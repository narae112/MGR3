package com.MGR.repository;

import com.MGR.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Inventory findByTicketIdAndDate(Long ticketId, LocalDate visitDate);
    List<Inventory> findAllByTicketId(Long ticketId);
}
