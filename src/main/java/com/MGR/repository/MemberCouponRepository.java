package com.MGR.repository;

import com.MGR.entity.Member;
import com.MGR.entity.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

}
