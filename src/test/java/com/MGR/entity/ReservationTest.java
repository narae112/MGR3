package com.MGR.entity;


import com.MGR.dto.MemberFormDto;
import com.MGR.repository.MemberRepository;
import com.MGR.repository.ReservationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReservationTest {
    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    public Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setPassword("1234");
        memberFormDto.setNickname("길동이");
        memberFormDto.setBirth("231202");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("예약-회원 엔티티 매핑 조회 테스트")
    public void findReservationAndMemberTest(){
        Member member = createMember();
        memberRepository.save(member);
        Reservation reservation = new Reservation();
        reservation.setMember(member);
        reservationRepository.save(reservation);

        em.flush();
        em.clear();

        Reservation saved = reservationRepository.findById(reservation.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(saved.getMember().getId(), member.getId());

    }


}