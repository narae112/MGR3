package com.MGR.repository;

import com.MGR.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Inventory findByTicketIdAndDate(Long ticketId, LocalDate visitDate);
    List<Inventory> findAllByTicketId(Long ticketId);

    Optional<Inventory> findByTicketId(Long ticketId);
}
