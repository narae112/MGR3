package com.MGR.controller;

import com.MGR.constant.ReservationStatus;
import com.MGR.dto.MemberCouponDto;
import com.MGR.dto.OrderTicketDto;
import com.MGR.entity.MemberCoupon;
import com.MGR.entity.Order;
import com.MGR.entity.OrderTicket;
import com.MGR.repository.MemberCouponRepository;
import com.MGR.repository.OrderRepository;
import com.MGR.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final MemberCouponRepository memberCouponRepository;

    // 결제 정보
    @GetMapping(value = {"/member/orders"})
    public String reservationList(@AuthenticationPrincipal PrincipalDetails member, Model model) {

        String email = member.getUsername();

        List<Order> orders = orderRepository.findOrders(email);
        // 로그인 한 멤버 주문 목록 조회

        List<OrderTicketDto> orderTicketList = new ArrayList<>();

        int totalPrice = 0; //합계 금액 구하려고

        List<String> orderNumList = new ArrayList<>(); //결제 후처리

        for (Order order : orders) { // 로그인 한 멤버 주문 목록들 중
            totalPrice += order.getAllTotalPrice();
            orderNumList.add(order.getOrderNum());
            if (order.getReservationStatus() == ReservationStatus.RESERVE) { // 예약 상태가 RESERVE 인 주문들만
                List<OrderTicket> orderTickets = order.getOrderTickets(); // OrderTicket 형식 리스트로 가져옴
                for (OrderTicket orderTicket : orderTickets) {
                    OrderTicketDto orderTicketDto = new OrderTicketDto(orderTicket); // OrderTicketDto 객체로 만들어서
                    orderTicketList.add(orderTicketDto); // 41번째 줄의 orderTicketList 에 더해줌
                }
            }
        }

        // 로그인한 멤버가 가진 쿠폰
        List<MemberCoupon> memberCouponList = memberCouponRepository.findAllByMemberId(member.getId());
        List<MemberCouponDto> memberCouponDtos = new ArrayList<>();

        for(MemberCoupon memberCoupon : memberCouponList) {
            MemberCouponDto memberCouponDto = new MemberCouponDto(memberCoupon);
            memberCouponDtos.add(memberCouponDto);
            System.out.println("memberCouponDto = " + memberCouponDto.getName());
        }

        System.out.println("orderNumList = " + orderNumList);
        System.out.println("totalPrice = " + totalPrice);


        model.addAttribute("orderTickets", orderTicketList); // OrderTicketDto 객체 담긴 리스트
        model.addAttribute("totalPrice", totalPrice); // 전체 가격
        model.addAttribute("orderNumList", orderNumList); // 주문 번호 리스트
        model.addAttribute("memberCouponList", memberCouponDtos); // 쿠폰 리스트

        return "/checkout";
    }

}