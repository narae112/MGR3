package com.MGR.repository;

import com.MGR.constant.LocationCategory;
import com.MGR.dto.MainTicketDto;
import com.MGR.dto.QMainTicketDto;
import com.MGR.dto.TicketSearchDto;
import com.MGR.entity.QImage;
import com.MGR.entity.QTicket;
import com.MGR.entity.Ticket;
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
public class TicketRepositoryCustomImpl implements TicketRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public TicketRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }



    private BooleanExpression searchLocationCategoryEq(LocationCategory searchLocationCategory) {
        return searchLocationCategory == null ? null : QTicket.ticket.locationCategory.eq(searchLocationCategory);
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

        return QTicket.ticket.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.equals("name", searchBy)) {
            return QTicket.ticket.name.like("%" + searchQuery + "%");
        }
        return null;
    }

    @Override
    public Page<Ticket> getAdminTicketPage(TicketSearchDto ticketSearchDto, Pageable pageable) {
        List<Ticket> content = queryFactory
                .selectFrom(QTicket.ticket)
                .where(regDtsAfter(ticketSearchDto.getSearchDateType()),
                        searchByLike(ticketSearchDto.getSearchBy(),
                                ticketSearchDto.getSearchQuery()))
                .orderBy(QTicket.ticket.id.desc())// 아이템을 아이디 기준으로 내림차순
                .offset(pageable.getOffset())
                //페이지 번호와 페이지 크기를 고려하여 결과를 가져올 시작 오프셋을 설정합니다.
                .limit(pageable.getPageSize()) //: 한 페이지에 표시될 아이템 수를 제한합니다.
                .fetch();//쿼리를 실행하고 결과를 가져온다.
        long total = queryFactory.select(Wildcard.count).from(QTicket.ticket)
                .where(regDtsAfter(ticketSearchDto.getSearchDateType()),
                        searchByLike(ticketSearchDto.getSearchBy(), ticketSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression ticketNameLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QTicket.ticket.name.like("%" + searchQuery + "%");
    }

    @Override
    public Page<MainTicketDto> getMainTicketPage(TicketSearchDto ticketSearchDto, Pageable pageable) {
        QTicket ticket = QTicket.ticket;
        QImage ticketImg = QImage.image;
        BooleanBuilder builder = new BooleanBuilder();

        // 검색 조건 추가
        builder.and(ticketImg.repImgYn.eq(true))
                .and(ticketNameLike(ticketSearchDto.getSearchQuery()));

        BooleanExpression periodCondition = remainingPeriodAfter(ticketSearchDto.getSearchPeriodType());
        if (periodCondition != null) {
            builder.and(periodCondition);
        }

        // 지역 구분 조건 추가
        BooleanExpression locationCategoryCondition = searchLocationCategoryEq(ticketSearchDto.getLocationCategory());
        if (locationCategoryCondition != null) {
            builder.and(locationCategoryCondition);
        }


        List<MainTicketDto> content = queryFactory
                .select(
                        new QMainTicketDto(
                                ticket.id,
                                ticket.name,
                                ticket.memo,
                                ticketImg.imgUrl,
                                ticket.startDate,
                                ticket.endDate,
                                ticket.locationCategory

                        )
                )
                .from(ticketImg)
                .join(ticketImg.ticket, ticket)
                .where(builder)
                .orderBy(ticket.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(Wildcard.count)
                .from(ticketImg)
                .join(ticketImg.ticket, ticket)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }


    //    남은 기간 조회
    private BooleanExpression remainingPeriodAfter(String searchPeriodType) {
        LocalDate now = LocalDate.now();

        if (searchPeriodType == null || StringUtils.equals("all", searchPeriodType)) {
            return QTicket.ticket.endDate.after(now);
        } else if (StringUtils.equals("1d", searchPeriodType)) {
            LocalDate futureDate = now.plusDays(1); // 1일 후
            return QTicket.ticket.endDate.between(now, futureDate);
        } else if (StringUtils.equals("1w", searchPeriodType)) {
            LocalDate futureDate = now.plusWeeks(1); // 1주일 후
            return QTicket.ticket.endDate.between(now, futureDate); // 1주일 후까지로 수정
        } else if (StringUtils.equals("1m", searchPeriodType)) {
            LocalDate futureDate = now.plusMonths(1); // 한 달 후
            return QTicket.ticket.endDate.between(now, futureDate); // 한 달 후까지로 수정
        } else if (StringUtils.equals("6m", searchPeriodType)) {
            LocalDate futureDate = now.plusMonths(6); // 6개월 후
            return QTicket.ticket.endDate.between(now, futureDate); // 6개월 후까지로 수정
        }

        return QTicket.ticket.endDate.after(now); // 기본 조건으로 현재 시간 이후
    }


}





