package com.MGR.repository;

import com.MGR.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    @Query("select o from Orders o " +
            "where o.member.email = :email " +
            "order by o.orderDate desc"
    )
    List<Orders> findOrders(@Param("email") String email); // 해당 멤버의 오더들
    Orders findByOrderNum(String orderNum);
}