package com.MGR.service;

import com.MGR.dto.OrderDto;
import com.MGR.dto.ReservationDtlDto;
import com.MGR.dto.ReservationOrderDto;
import com.MGR.dto.ReservationTicketDto;
import com.MGR.entity.*;
import com.MGR.repository.*;
import io.netty.channel.EventLoopException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Transactional
@Service
public class ReservationService {

    private final TicketRepository ticketRepository;
    private final InventoryRepository inventoryRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTicketRepository reservationTicketRepository;
    private final OrderService orderService;

    // 예약 내역에 추가
    public Long addReservation(ReservationTicketDto reservationTicketDto, String email) {
        Ticket ticket = ticketRepository.findById(reservationTicketDto.getTicketId())
                .orElseThrow(EntityNotFoundException::new);
        // 예약할 티켓을 데이터베이스에서 찾는다

        Inventory inventory = inventoryRepository.findByTicketIdAndDate(reservationTicketDto.getTicketId(), LocalDate.parse(reservationTicketDto.getVisitDate()));
        // 티켓 아이디와 방문날짜로 해당 티켓의 해당 날짜 재고 정보를 가져온다

        inventory.removeQuantity(reservationTicketDto.getTicketCount());
        // 재고 체크(남아있는 재고보다 주문수량이 많으면 예약 X)

        Optional<Member> member = memberRepository.findByEmail(email);
        // 이메일을 이용, 로그인 한 멤버를 데이터베이스에서 찾는다

        Reservation reservation = reservationRepository.findByMemberId(member.get().getId());
        // 데이터베이스에서 찾은 멤버의 아이디로 예약 내역을 가지고 있는지 찾는다

        if(reservation == null) {
            reservation = Reservation.createReservation(member.orElse(null));
            reservationRepository.save(reservation);
        } // 로그인 된 회원이 예약 내역을 가지고 있는지 확인하고 없으면 만듦

        List<ReservationTicket> savedReservationTickets = reservationTicketRepository.findByReservationIdAndTicketId(reservation.getId(), ticket.getId());

        // 예약하려는 티켓이 이미 예약한 티켓 리스트에 있는지 확인한다
        for(ReservationTicket savedReservationTicket : savedReservationTickets) {
            // 이미 예약한 티켓 리스트에 있고 방문날짜가 동일하면
            if(savedReservationTicket != null && savedReservationTicket.getVisitDate().equals(reservationTicketDto.getVisitDate())) {
                savedReservationTicket.addCount(reservationTicketDto.getAdultCount(), reservationTicketDto.getChildCount()); // 수량만 증가

                return savedReservationTicket.getId(); // 예약 티켓 아이디 반환
            }
        }

        ReservationTicket reservationTicket = ReservationTicket.createReservationTicket(reservation, ticket,
                reservationTicketDto.getAdultCount(), reservationTicketDto.getChildCount(), reservationTicketDto.getVisitDate());

        reservationTicketRepository.save(reservationTicket); // 새로운 예약 티켓 저장

        return reservationTicket.getId();
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

    // 티켓 수량 수정
    public void updateReservationTicketCount(Long reservationTicketId, int adultCount, int childCount) {
        ReservationTicket reservationTicket = reservationTicketRepository.findById(reservationTicketId)
                .orElseThrow(EntityNotFoundException::new);

        Inventory inventory = inventoryRepository.findByTicketIdAndDate(reservationTicket.getTicket().getId(), LocalDate.parse(reservationTicket.getVisitDate()));
        // 인벤토리 데이터베이스에서 해당 티켓 찾기

        int allReserveCount = adultCount + childCount;
        // 성인티켓 수량 + 아동티켓 수량

        if(reservationTicket.getAllCount() < allReserveCount) { // 수정된 전체 수량이 원래 예약한 전체 수량보다 커지면
            int result = allReserveCount - reservationTicket.getAllCount(); // 추가한 갯수만큼
            inventory.removeQuantity(result); // 재고에서 빼준다
        } else if(reservationTicket.getAllCount() > allReserveCount) { // 수정된 전체 수량이 원래 예약한 전체 수량보다 적어지면
            int result = reservationTicket.getAllCount() - allReserveCount; // 뺀 갯수만큼
            inventory.addQuantity(result); // 재고에 더해준다
        }

        if(reservationTicket.getAdultCount() != adultCount && reservationTicket.getChildCount() == childCount) { // 수량의 변동이 어른티켓만 있으면
            reservationTicket.updateAdultCount(adultCount);
        } else if(reservationTicket.getAdultCount() == adultCount && reservationTicket.getChildCount() != childCount) { // 수량의 변동이 아동티켓만 있으면
            reservationTicket.updateChildCount(childCount);
        } // else if(reservationTicket.getAdultCount() != adultCount && reservationTicket.getChildCount() != childCount) { // 수량의 변동이 양쪽 다 있으면
//            reservationTicket.updateAllCount(adultCount, childCount);
//        }

        reservationTicketRepository.save(reservationTicket); // 데이터베이스에 수정된 내용 저장
    }

    // 예약 취소
    public void cancelReservation(Long reservationId) {
        ReservationTicket reservationTicket = reservationTicketRepository.findById(reservationId)
                                                                    .orElseThrow(EntityNotFoundException::new);

        reservationTicket.cancelReservation();

        Inventory inventory = inventoryRepository.findByTicketIdAndDate(reservationTicket.getTicket().getId(), LocalDate.parse(reservationTicket.getVisitDate()));

        inventory.addQuantity(reservationTicket.getAllCount()); // 예약 되어 있던 티켓 갯수만큼 재고에 다시 더해준다
    }

    // 결제
    public Long orderReservationTicket(List<ReservationOrderDto> reservationOrderDtoList, String email) {
        // reservationOrderDtoList : 결제 할 (구매자가 선택한) 예약티켓 아이디들
        List<OrderDto> orderDtoList = new ArrayList<>();

        for(ReservationOrderDto reservationOrderDto : reservationOrderDtoList) {
            //ReservationOrderDto 객체 만들어서 reservationTicket 정보 가져오기
            ReservationTicket reservationTicket = reservationTicketRepository.findById(reservationOrderDto.getReservationTicketId())
                    .orElseThrow(EntityNotFoundException::new);

            // OrderDto 객체 만들어서 reservationTicket 정보 넣기
            OrderDto orderDto = new OrderDto();

            orderDto.setTicketId(reservationTicket.getTicket().getId()); // 예약한 티켓의 티켓아이디
            orderDto.setReservationTicketId(reservationTicket.getId()); // 예약티켓 아이디
            orderDto.setAdultCount(reservationTicket.getAdultCount()); // 예약한 성인 티켓 갯수
            orderDto.setChildCount(reservationTicket.getChildCount()); // 예약한 아동 티켓 갯수
            orderDto.setVisitDate(LocalDate.parse(reservationTicket.getVisitDate())); // 예약한 티켓의 방문일

            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);

//        for(ReservationOrderDto reservationOrderDto : reservationOrderDtoList) {
//            ReservationTicket reservationTicket = reservationTicketRepository.findById(reservationOrderDto.getReservationTicketId())
//                    .orElseThrow(EventLoopException::new);
//            reservationTicketRepository.delete(reservationTicket);
//        }

        return orderId;
    }

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