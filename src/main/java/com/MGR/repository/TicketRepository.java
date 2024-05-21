package com.MGR.repository;

import com.MGR.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> ,
        QuerydslPredicateExecutor<Ticket>, TicketRepositoryCustom{

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

}
