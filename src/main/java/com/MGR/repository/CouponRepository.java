package com.MGR.repository;

import com.MGR.constant.CouponType;
import com.MGR.entity.Coupon;
import com.MGR.entity.Member;
import com.MGR.entity.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long>,
        QuerydslPredicateExecutor<Coupon>, CouponRepositoryCustom {

    Optional<Coupon> findByNameAndStartDateAndEndDate(String name, LocalDate startDate, LocalDate endDate);
    List<Coupon> findByEndDateBefore(LocalDate currentDate);

}
