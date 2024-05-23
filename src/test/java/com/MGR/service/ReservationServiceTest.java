package com.MGR.service;

import com.MGR.constant.TicketCategory;
import com.MGR.dto.MemberFormDto;
import com.MGR.dto.ReservationTicketDto;
import com.MGR.entity.Member;
import com.MGR.entity.ReservationTicket;
import com.MGR.entity.Ticket;
import com.MGR.repository.MemberRepository;
import com.MGR.repository.ReservationTicketRepository;
import com.MGR.repository.TicketRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReservationServiceTest {
    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationTicketRepository reservationTicketRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    public Ticket saveTicket() {
        Ticket ticket = new Ticket();
        ticket.setName("테스트티켓");
        ticket.setPrice(40000);
        ticket.setMemo("티켓설명");
        ticket.setTicketCategory(TicketCategory.ADULT);
        ticket.setLocation("Busan");
        ticket.setStartDate("2024-05-23");
        ticket.setEndDate("2024-05-30");

        return ticketRepository.save(ticket);
    }

    public Member createMember() {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setPassword("1234");
        memberFormDto.setNickname("길동이");
        memberFormDto.setBirth("231202");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    public void addReservationTest() {
        Ticket ticket = saveTicket();
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        ReservationTicketDto reservationTicketDto = new ReservationTicketDto();
        reservationTicketDto.setTicketId(ticket.getId());
        reservationTicketDto.setTicketCount(5);

        Long reservationTicketId = reservationService.addReservation(reservationTicketDto, savedMember.getEmail()); // reservationTicket.getId() 반환함

        ReservationTicket reservationTicket = reservationTicketRepository.findById(reservationTicketId)
                .orElseThrow(EntityNotFoundException::new);

        assertEquals(reservationTicket.getId(), reservationTicket.getTicket().getId());
    }




}