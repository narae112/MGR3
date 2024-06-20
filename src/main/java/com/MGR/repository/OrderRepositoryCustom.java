package com.MGR.repository;

import com.MGR.dto.OrderSearchDto;
import com.MGR.entity.Order;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;

public interface OrderRepositoryCustom {
    Page<Order> getOrderPage(OrderSearchDto orderSearchDto, Pageable pageable);
}
