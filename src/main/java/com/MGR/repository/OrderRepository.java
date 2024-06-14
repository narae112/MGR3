package com.MGR.repository;

import com.MGR.dto.OrderListDto;
import com.MGR.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.net.ContentHandler;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o " +
            "where o.member.email = :email " +
            "order by o.orderDate desc"
    )
    List<Order> findOrders(@Param("email") String email); // 해당 멤버의 오더들
    Order findByOrderNum(String orderNum);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.member.id = :memberId")
    int countByMemberId(@Param("memberId") Long memberId);

    Page<OrderListDto> findAllOrderListDtoByMemberId(Long memberId, Pageable pageable);
//    Page<Order> findAllByMemberId(Long memberId, Pageable pageable);
    long countAllByMemberId(Long memberId);

    Page<Order> findAllByMemberId(Long memberId, Pageable pageable);

    List<Order> findAllByMemberId(Long memberId);
}