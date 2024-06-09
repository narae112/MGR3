package com.MGR.repository;

import com.MGR.constant.LocationCategory;
import com.MGR.constant.TicketCategory;
import com.MGR.entity.Coupon;
import com.MGR.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> ,
        QuerydslPredicateExecutor<Ticket>, TicketRepositoryCustom{
    List<Ticket> findByEndDateBefore(LocalDate currentDate);

//    @Query(value = "SELECT * FROM table_name WHERE column_name LIKE %?1% ORDER BY price DESC", nativeQuery = true)
//    List<Ticket> findByMemoContainingOrderByPriceDesc(String memo);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
