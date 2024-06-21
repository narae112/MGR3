package com.MGR.repository;

import com.MGR.dto.OrderSearchDto;
import com.MGR.entity.Order;
import com.MGR.entity.QOrder;
import com.MGR.entity.QTicket;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {


    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Autowired
    public OrderRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Order> getOrderPage(OrderSearchDto orderSearchDto, Pageable pageable) {
        List<Order> content = queryFactory
                .selectFrom(QOrder.order)
                .where(applyFilters(orderSearchDto))
                .orderBy(QOrder.order.orderDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(Wildcard.count).from(QOrder.order)
                .where(applyFilters(orderSearchDto))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression applyFilters(OrderSearchDto orderSearchDto) {
        BooleanExpression filter = searchDate(orderSearchDto);
        // 다른 필터가 있다면 여기서 추가
        return filter;
    }

    private BooleanExpression searchDate(OrderSearchDto orderSearchDto) {
        LocalDateTime now = LocalDateTime.now();

        if (orderSearchDto == null) {
            return null;
        }

        if (orderSearchDto.getStartDate() != null && orderSearchDto.getEndDate() != null) {
            return QOrder.order.orderDate.between(
                    orderSearchDto.getStartDate().atStartOfDay(),
                    orderSearchDto.getEndDate().atTime(23, 59, 59));
        }

        if (StringUtils.equals("all", orderSearchDto.getSearchDateType())) {
            return null;
        } else if (StringUtils.equals("1d", orderSearchDto.getSearchDateType())) {
            return QOrder.order.orderDate.after(now.minusDays(1));
        } else if (StringUtils.equals("1w", orderSearchDto.getSearchDateType())) {
            return QOrder.order.orderDate.after(now.minusWeeks(1));
        } else if (StringUtils.equals("1m", orderSearchDto.getSearchDateType())) {
            return QOrder.order.orderDate.after(now.minusMonths(1));
        } else if (StringUtils.equals("6m", orderSearchDto.getSearchDateType())) {
            return QOrder.order.orderDate.after(now.minusMonths(6));
        } else if (StringUtils.equals("1y", orderSearchDto.getSearchDateType())) {
            return QOrder.order.orderDate.after(now.minusYears(1));
        }

        return null;
    }

}
