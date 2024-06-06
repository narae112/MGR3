package com.MGR.repository;

import com.MGR.dto.*;
import com.MGR.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CouponRepositoryCustomImpl implements CouponRepositoryCustom {
    private JPAQueryFactory queryFactory;

    public CouponRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression regDtsAfter(String searchDateType) {

        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1); //1일이내
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1); //일주일
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);//한달
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);//6개월
        }

        return QCoupon.coupon.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.equals("name", searchBy)) {
            return QCoupon.coupon.name.like("%" + searchQuery + "%");
        }
        return null;
    }

    @Override
    public Page<Coupon> getAdminCouponPage(CouponSearchDto couponSearchDto, Pageable pageable) {
        List<Coupon> content = queryFactory
                .selectFrom(QCoupon.coupon)
                .where(regDtsAfter(couponSearchDto.getSearchDateType()),
                        searchByLike(couponSearchDto.getSearchBy(),
                                couponSearchDto.getSearchQuery()))
                .orderBy(QCoupon.coupon.id.desc())// 아이템을 아이디 기준으로 내림차순
                .offset(pageable.getOffset())
                //페이지 번호와 페이지 크기를 고려하여 결과를 가져올 시작 오프셋을 설정합니다.
                .limit(pageable.getPageSize()) //: 한 페이지에 표시될 아이템 수를 제한합니다.
                .fetch();//쿼리를 실행하고 결과를 가져온다.
        long total = queryFactory.select(Wildcard.count).from(QCoupon.coupon)
                .where(regDtsAfter(couponSearchDto.getSearchDateType()),
                        searchByLike(couponSearchDto.getSearchBy(), couponSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression couponNameLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QCoupon.coupon.name.like("%" + searchQuery + "%");
    }

    @Override
    public Page<CouponMainDto> getCouponMainPage(CouponSearchDto couponSearchDto, Pageable pageable) {
        QCoupon coupon = QCoupon.coupon;
        QImage couponImg = QImage.image;
        BooleanBuilder builder = new BooleanBuilder();

        // 검색 조건 추가
        builder.and(couponImg.repImgYn.eq(true))
                .and(couponNameLike(couponSearchDto.getSearchQuery()));

        BooleanExpression periodCondition = remainingPeriodAfter(couponSearchDto.getSearchPeriodType());
        if (periodCondition != null) {
            builder.and(periodCondition);
        }

        List<CouponMainDto> content = queryFactory
                .select(
                        new QCouponMainDto(
                                coupon.id,
                                coupon.name,
                                couponImg.imgUrl,
                                coupon.startDate,
                                coupon.endDate,
                                coupon.couponContent
                        )
                )
                .from(couponImg)
                .join(couponImg.coupon, coupon)
                .where(builder)
                .orderBy(coupon.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(Wildcard.count)
                .from(couponImg)
                .join(couponImg.coupon, coupon)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    // 남은 기간 조회
    private BooleanExpression remainingPeriodAfter(String searchPeriodType) {
        LocalDate now = LocalDate.now();

        if (searchPeriodType == null || StringUtils.equals("all", searchPeriodType)) {
            return QCoupon.coupon.endDate.after(now);
        } else if (StringUtils.equals("1d", searchPeriodType)) {
            LocalDate futureDate = now.plusDays(1); // 1일 후
            return QCoupon.coupon.endDate.between(now, futureDate);
        } else if (StringUtils.equals("1w", searchPeriodType)) {
            LocalDate futureDate = now.plusWeeks(1); // 1주일 후
            return QCoupon.coupon.endDate.between(now, futureDate); // 1주일 후까지로 수정
        } else if (StringUtils.equals("1m", searchPeriodType)) {
            LocalDate futureDate = now.plusMonths(1); // 한 달 후
            return QCoupon.coupon.endDate.between(now, futureDate); // 한 달 후까지로 수정
        } else if (StringUtils.equals("6m", searchPeriodType)) {
            LocalDate futureDate = now.plusMonths(6); // 6개월 후
            return QCoupon.coupon.endDate.between(now, futureDate); // 6개월 후까지로 수정
        }

        return QCoupon.coupon.endDate.after(now); // 기본 조건으로 현재 시간 이후
    }
}
