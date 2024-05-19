package com.MGR.repository;

import com.MGR.constant.TicketCategory;
import com.MGR.entity.QTicket;
import com.MGR.entity.Ticket;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class TicketRepositoryTest {
    @Autowired
    TicketRepository ticketRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("티켓 저장 테스트")
        public void createTicketTest(){
        Ticket ticket = new Ticket();
        ticket.setName("테스트 티켓");
        ticket.setPrice(100000);
        ticket.setTicketCategory(TicketCategory.PUBLIC);
        ticket.setRegTime(LocalDateTime.now());
        ticket.setUpdateTime(LocalDateTime.now());
        ticket.setLocation("부산");
        Ticket savedTicket = ticketRepository.save(ticket);
        System.out.println(savedTicket.toString());
        }

        public void createTicketList(){
        for(int i =1; i<10; i++){
            Ticket ticket = new Ticket();
            ticket.setName("테스트 상품"+i);
            ticket.setPrice(10000+i);
            ticket.setMemo("롯데월드 상품입니다");
            ticket.setLocation("부산");
            ticket.setTicketCategory(TicketCategory.PUBLIC);
            ticket.setRegTime(LocalDateTime.now());
            ticket.setUpdateTime(LocalDateTime.now());
            Ticket savedTicket = ticketRepository.save(ticket);
        }
        }

    @Test
    @DisplayName("상품 조회 테스트")
    public void findByNameTest(){
        this.createTicketList();
        List<Ticket> ticketList = ticketRepository.findByName("테스트 상품1");
         for(Ticket ticket : ticketList){
             System.out.println(ticket.toString());
         }
    }
    @Test
    @DisplayName("Querydsl 조회테스트1")
    public void queryDslTest(){
        this.createTicketList();
        JPAQueryFactory queryFactory =  new JPAQueryFactory(em);
        //쿼리객체생성
        QTicket qTicket = QTicket.ticket;
        JPAQuery<Ticket> query = queryFactory.selectFrom(qTicket)
                .where(qTicket.ticketCategory.eq(TicketCategory.PUBLIC))
                .where(qTicket.memo.like("%"+"테스트 상품 상세 설명"+"%"))
                .orderBy(qTicket.price.desc());

        List<Ticket> TicketList = query.fetch();

        for(Ticket ticket :TicketList){
            System.out.println(ticket.toString());
        }
    }

}