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

        Optional<Member> member = memberRepository.findByEmail(email);

        Reservation reservation = reservationRepository.findByMemberId(member.get().getId());

        if(reservation == null) {
            reservation = Reservation.createReservation(member.orElse(null));
            reservationRepository.save(reservation);
        } // 로그인 된 회원이 예약 내역을 가지고 있는지 확인하고 없으면 만듦

        ReservationTicket savedReservationTicket = reservationTicketRepository.findByReservationIdAndTicketId(reservation.getId(), ticket.getId());
        // 예약할 티켓이 이미 예약되어있는 티켓이면

        if(savedReservationTicket != null) {
            savedReservationTicket.addCount(reservationTicketDto.getTicketCount()); // 수량만 증가

            return savedReservationTicket.getId(); // 예약 티켓 아이디 반환
        } else { // 예약 된 티켓이 아니면
            ReservationTicket reservationTicket = ReservationTicket.createReservationTicket(reservation, ticket,
                    reservationTicketDto.getTicketCount(), reservationTicketDto.getVisitDate());
            reservationTicketRepository.save(reservationTicket); // 새로 예약

            return reservationTicket.getId();
        }
    }

    // 예약 내역
    @Transactional(readOnly = true)
    public List<ReservationDtlDto> getReservationList(String email){

        List<ReservationDtlDto> reservationDtlDtoList = new ArrayList<>();
        Optional<Member> member = memberRepository.findByEmail(email);

        Reservation reservation = reservationRepository.findByMemberId(member.get().getId());
        if(reservation == null){
            return reservationDtlDtoList;
        }
        reservationDtlDtoList = reservationTicketRepository.findReservationDtlDtoList(reservation.getId());
        return reservationDtlDtoList;
    }

    // 티켓 수량 update
    public void updateReservationTicketCount(Long reservationTicketId, int ticketCount) {
        ReservationTicket reservationTicket = reservationTicketRepository.findById(reservationTicketId)
                .orElseThrow(EntityNotFoundException::new);

        reservationTicket.updateCount(ticketCount);
    }

    // 예약 취소
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(EntityNotFoundException::new);
        reservation.cancelReservation();
    }

    // 결제

    // 검증
    @Transactional(readOnly = true)
    public boolean validateReserveTicket(Long reservationTicketId, String email) {
        Optional<Member> curmember = memberRepository.findByEmail(email); // 현재 로그인 된 회원
        ReservationTicket reservationTicket = reservationTicketRepository.findById(reservationTicketId)
                .orElseThrow(EntityNotFoundException::new);

        Member savedmember = reservationTicket.getReservation().getMember(); // 예약된 티켓의 회원 정보

        if(!StringUtils.equals(curmember.get().getEmail(), savedmember.getEmail())) { // 현재 로그인 된 회원의 이메일과 예약 티켓 회원 정보의 이메일이 다르면
            return false;
        }
        return true;
    }
}
