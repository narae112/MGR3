package com.MGR.repository;

import com.MGR.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDate;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long>,
        QuerydslPredicateExecutor<Coupon>, CouponRepositoryCustom {

    Optional<Coupon> findByNameAndDiscountRateAndStartDateAndEndDate(
        String name, int discountRate, LocalDate startDate, LocalDate endDate
    );

}
