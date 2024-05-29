package com.MGR.service;

import com.MGR.dto.ReservationDtlDto;
import com.MGR.dto.ReservationTicketDto;
import com.MGR.entity.Member;
import com.MGR.entity.Reservation;
import com.MGR.entity.ReservationTicket;
import com.MGR.entity.Ticket;
import com.MGR.repository.MemberRepository;
import com.MGR.repository.ReservationRepository;
import com.MGR.repository.ReservationTicketRepository;
import com.MGR.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final TicketRepository ticketRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTicketRepository reservationTicketRepository;
    // private final OrderService orderService; // 결제

    // 예약 내역에 추가
    public Long addReservation(ReservationTicketDto reservationTicketDto, String email) {
        Ticket ticket = ticketRepository.findById(reservationTicketDto.getTicketId())
                .orElseThrow(EntityNotFoundException::new);
        // 예약할 티켓을 데이터베이스에서 찾는다
        Optional<Member> member = memberRepository.findByEmail(email);
        // 이메일을 이용, 로그인 한 멤버를 데이터베이스에서 찾는다
        Reservation reservation = reservationRepository.findByMemberId(member.get().getId());
        // 데이터베이스에서 찾은 멤버의 아이디로 예약 내역을 가지고 있는지 찾는다
        if(reservation == null) {
            reservation = Reservation.createReservation(member.orElse(null));
            reservationRepository.save(reservation);
        } // 로그인 된 회원이 예약 내역을 가지고 있는지 확인하고 없으면 만듦

        ReservationTicket savedReservationTicket = reservationTicketRepository.findByReservationIdAndTicketId(reservation.getId(), ticket.getId());
        // 멤버가 가진 예약 내역과 예약할 티켓 아이디를 가지고 예약할 티켓이 데이터베이스에 있는지 찾는다

        // 예약할 티켓이 이미 예약되어있는 티켓이면서 방문예정일이 같으면
        if(savedReservationTicket != null && savedReservationTicket.getVisitDate().equals(reservationTicketDto.getVisitDate())) {
            savedReservationTicket.addCount(reservationTicketDto.getTicketCount()); // 수량만 증가
            return savedReservationTicket.getId(); // 예약 티켓 아이디 반환
        } else {
            ReservationTicket reservationTicket = ReservationTicket.createReservationTicket(reservation, ticket,
                    reservationTicketDto.getTicketCount(), reservationTicketDto.getVisitDate());
            reservationTicketRepository.save(reservationTicket); // 새로 예약

            return reservationTicket.getId();
        }
    }

    // 예약 내역 불러오기
    @Transactional(readOnly = true)
    public Page<ReservationDtlDto> getReservationList(String email, Pageable pageable) {
        Optional<Member> member = memberRepository.findByEmail(email);
        Reservation reservation = reservationRepository.findByMemberId(member.get().getId());

        List<ReservationDtlDto> reservationDtlDtos = new ArrayList<>();
        reservationDtlDtos = reservationTicketRepository.findReservations(reservation.getId(), pageable);
        // 주문 목록 조회
        Long totalCount = reservationTicketRepository.countReservation(reservation.getId());
        // 총 주문 갯수

        return new PageImpl<ReservationDtlDto>(reservationDtlDtos, pageable, totalCount);
    }

    // 티켓 수량 update
    public void updateReservationTicketCount(Long reservationTicketId, int ticketCount) {
        ReservationTicket reservationTicket = reservationTicketRepository.findById(reservationTicketId)
                .orElseThrow(EntityNotFoundException::new);

        reservationTicket.updateCount(ticketCount);
    }

    // 예약 취소
    public void cancelReservation(Long reservationId) {
        ReservationTicket reservationTicket = reservationTicketRepository.findById(reservationId)
                .orElseThrow(EntityNotFoundException::new);
        reservationTicket.cancelReservation();
    }

    // 결제

    // 검증
    @Transactional(readOnly = true)
    public boolean validateReserveTicket(Long reservationTicketId, String email) {
        Optional<Member> curMember = memberRepository.findByEmail(email); // 현재 로그인 된 회원
        ReservationTicket reservationTicket = reservationTicketRepository.findById(reservationTicketId)
                .orElseThrow(EntityNotFoundException::new);

        Member savedmember = reservationTicket.getReservation().getMember(); // 예약된 티켓의 회원 정보

        if(!StringUtils.equals(curMember.get().getEmail(), savedmember.getEmail())) { // 현재 로그인 된 회원의 이메일과 예약 티켓 회원 정보의 이메일이 다르면
            return false;
        }
        return true;
    }
}
