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

    @Query(value="select * from ticket i where i.memo like " +
        "%:memo% order by i.price desc", nativeQuery = true)
    List<Ticket> findByMemoByNative(@Param("memo") String memo);
    Optional<Ticket> findByNameAndAdultPriceAndChildPriceAndMemoAndStartDateAndEndDateAndLocationCategory(
            String name, int adultPrice, int childPrice, String memo,
            LocalDate startDate, LocalDate endDate, LocationCategory locationCategory
    );
    boolean existsByName(String name);
}
