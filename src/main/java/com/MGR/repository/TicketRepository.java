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
    List<Ticket> findByName(String name);
    List<Ticket> findByNameOrMemo(String name, String memo);
    List<Ticket> findByPriceLessThan(Integer price);
    //특정 가격보다 저렴한 티켓
    List<Ticket> findByPriceLessThanOrderByPriceDesc(Integer price);

    @Query("select i from Ticket i where i.memo like %:memo% order by i.price desc")
    List<Ticket> findByMemo(@Param("memo") String memo);


    @Query(value="select * from ticket i where i.memo like " +
        "%:memo% order by i.price desc", nativeQuery = true)
    List<Ticket> findByMemoByNative(@Param("memo") String memo);
    Optional<Ticket> findByNameAndPriceAndMemoAndTicketCategoryAndStartDateAndEndDateAndLocationCategory(
            String name, int price, String memo, TicketCategory ticketCategory,
            LocalDate startDate, LocalDate endDate, LocationCategory locationCategory
    );

    Optional<Ticket> findByPriceAndLocationCategoryAndMemoAndTicketCategory(
            int price, LocationCategory locationCategory, String memo, TicketCategory ticketCategory
    );
}
