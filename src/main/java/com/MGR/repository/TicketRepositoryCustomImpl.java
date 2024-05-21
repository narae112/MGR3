package com.MGR.repository;

import com.MGR.constant.TicketCategory;
import com.MGR.dto.MainTicketDto;
import com.MGR.dto.QMainTicketDto;
import com.MGR.dto.TicketSearchDto;
import com.MGR.entity.QImage;
import com.MGR.entity.QTicket;
import com.MGR.entity.Ticket;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TicketRepositoryCustomImpl implements TicketRepositoryCustom {

    private JPAQueryFactory queryFactory;
    public TicketRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }
    private BooleanExpression searchTicketCategoryEq(TicketCategory searchTicketCategory){
        return searchTicketCategory == null ? null : QTicket.ticket.ticketCategory.eq(searchTicketCategory);
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

    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if(StringUtils.equals("name", searchBy)){
            return QTicket.ticket.name.like("%" + searchQuery + "%");
        }
        return null;
    }
    @Override
    public Page<Ticket> getAdminTicketPage(TicketSearchDto ticketSearchDto, Pageable pageable) {
        List<Ticket> content = queryFactory
                .selectFrom(QTicket.ticket)
                .where(regDtsAfter(ticketSearchDto.getSearchDateType()),
                        searchTicketCategoryEq(ticketSearchDto.getTicketCategory()),
                        searchByLike(ticketSearchDto.getSearchBy(),
                                ticketSearchDto.getSearchQuery()))
                .orderBy(QTicket.ticket.id.desc())// 아이템을 아이디 기준으로 내림차순
                .offset(pageable.getOffset())
                //페이지 번호와 페이지 크기를 고려하여 결과를 가져올 시작 오프셋을 설정합니다.
                .limit(pageable.getPageSize()) //: 한 페이지에 표시될 아이템 수를 제한합니다.
                .fetch();//쿼리를 실행하고 결과를 가져온다.
        long total = queryFactory.select(Wildcard.count).from(QTicket.ticket)
                .where(regDtsAfter(ticketSearchDto.getSearchDateType()),
                        searchTicketCategoryEq(ticketSearchDto.getTicketCategory()),
                        searchByLike(ticketSearchDto.getSearchBy(), ticketSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
    private BooleanExpression ticketNameLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QTicket.ticket.name.like("%" + searchQuery + "%");
    }
    @Override
    public Page<MainTicketDto> getMainTicketPage(TicketSearchDto ticketSearchDto, Pageable pageable) {
        QTicket ticket = QTicket.ticket;
        QImage ticketImg = QImage.image;
        List<MainTicketDto> content = queryFactory
                .select(
                        new QMainTicketDto(
                                ticket.id,
                                ticket.name,
                                ticket.memo,
                                ticketImg.imgUrl,
                                ticket.price
                        )
                )
                .from(ticketImg)
                .join(ticketImg.ticket, ticket)
                .where(ticketImg.repImgYn.eq(true))
                .where(ticketNameLike(ticketSearchDto.getSearchQuery()))
                .orderBy(ticket.id.desc())
                .offset(pageable.getOffset()) //가져올 데이터의 시작 오프셋
                .limit(pageable.getPageSize()) //한페이지당 가져올 데이터의 갯수
                .fetch();
        //페이지 크기가 5이고 현재페이지가 2이면 pageable.getOffset() 5를 반환
        // limit(pageable.getPageSize() 는 5를 반환하여 6번째부터 10번째가지 데이터를 가져옴

        long total = queryFactory
                .select(Wildcard.count)
                .from(ticketImg)
                .join(ticketImg.ticket, ticket)
                .where(ticketImg.repImgYn.eq(true))
                .where(ticketNameLike(ticketSearchDto.getSearchQuery()))
                .fetchOne()
                ;


        return  new PageImpl<>(content, pageable, total);
    }
}







