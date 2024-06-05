package com.MGR.service;

import com.MGR.constant.ReservationStatus;
import com.MGR.dto.OrderDto;
import com.MGR.entity.*;
import com.MGR.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final TicketRepository ticketRepository;
    private final OrderTicketRepository orderTicketRepository;
    private final ReservationTicketRepository reservationTicketRepository;

    // 이용자가 결제 요청한 정보 저장
    public Long orders(List<OrderDto> orderDtoList, String email) {
        // orderDtoList : 주문 목록(에약한 티켓의 티켓아이디와 방문일, 수량이 담긴)
        Optional<Member> member = memberRepository.findByEmail(email);
        // 주어진 이메일 주소로 회원 레포지토리에서 회원을 가져옴
        List<OrderTicket> orderTicketList = new ArrayList<>();
        // 결제할 티켓을 보관할 빈 목록을 초기화
        for(OrderDto orderDto : orderDtoList) {
            Ticket ticket = ticketRepository.findById(orderDto.getTicketId())
                    .orElseThrow(EntityNotFoundException::new);
            // orderDto 객체에 대해 해당하는 티켓 아이디를 사용하여 데이터베이스에서 티켓 정보를 가지고 옴
            ReservationTicket reservationTicket = reservationTicketRepository.findById(orderDto.getReservationTicketId())
                    .orElseThrow(EntityNotFoundException::new);
            OrderTicket orderTicket = OrderTicket.createOrderTicket(ticket, reservationTicket,
                                                                    orderDto.getAdultCount(), orderDto.getChildCount(), orderDto.getVisitDate());
            // createOrderTicket : 검색된 티켓을 사용하여 orderDto 에서 지정된 수량을 사용, orderTicket 객체 생성

            orderTicketRepository.save(orderTicket);

            orderTicketList.add(orderTicket);
            // 생성된 orderTicket 을 주문 목록에 더함
        }
        Order order = Order.createOder(member.orElseThrow(), orderTicketList);
        // 모든 주문 항목이 처리되면 회원과 주문 아이템 목록을 사용하여 Order 객체 생성
        orderRepository.save(order);
        // 생성된 주문 저장
        return order.getId();
        // 저장된 주문 아이디 반환
    }

    public void changeStatus(Long id) {
        Order order = orderRepository.findById(id).get();
        order.setReservationStatus(ReservationStatus.PAYED);
        orderRepository.save(order);
    }

}