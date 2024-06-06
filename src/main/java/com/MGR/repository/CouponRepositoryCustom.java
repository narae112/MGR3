package com.MGR.repository;

import com.MGR.dto.CouponMainDto;
import com.MGR.dto.CouponSearchDto;
import com.MGR.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponRepositoryCustom {
    Page<Coupon> getAdminCouponPage(CouponSearchDto couponSearchDtoSearchDto, Pageable pageable);
    Page<CouponMainDto> getCouponMainPage(CouponSearchDto couponSearchDto, Pageable pageable);
}
