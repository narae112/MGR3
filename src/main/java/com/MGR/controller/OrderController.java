package com.MGR.controller;

import com.MGR.constant.ReservationStatus;
import com.MGR.dto.MemberCouponDto;
import com.MGR.dto.OrderDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final MemberCouponRepository memberCouponRepository;

    // 결제 정보
    @GetMapping(value = {"/member/orders/{orderId}"})
    public String reservationList(@AuthenticationPrincipal PrincipalDetails member,
                                  @PathVariable("orderId") Long orderId, Model model) {

//        String email = member.getUsername();

//        List<Order> orders = orderRepository.findOrders(email);
        List<String> orderNumList = new ArrayList<>(); //결제 후처리
        Order order = orderRepository.findById(orderId).get();
        List<OrderTicket> orderTicketList = orderRepository.findById(orderId).get().getOrderTickets();
        List<OrderTicketDto> orderTickets = new ArrayList<>();
        int totalPrice = orderRepository.findById(orderId).get().getAllTotalPrice();
        String orderNum = order.getOrderNum();
        System.out.println("orderNum1 = " + orderNum);
        for (OrderTicket orderTicket : orderTicketList) {
            orderNumList.add(orderNum);
            System.out.println("orderNum2 = " + orderNum);
            OrderTicketDto dto = new OrderTicketDto(orderTicket);
            orderTickets.add(dto);
        }
        System.out.println(orderNumList.size());
//        List<OrderTicketDto> orderTicketList = new ArrayList<>();
//        int totalPrice = 0; //합계 금액 구하려고
//        List<String> orderNumLIst = new ArrayList<>(); //결제 후처리
//        for (Order order : orders) {
//            totalPrice += order.getTotalPrice();
//            orderNumLIst.add(order.getOrderNum());
//            if (order.getReservationStatus() == ReservationStatus.RESERVE) {
//                List<OrderTicket> orderTickets = order.getOrderTickets();
//                for (OrderTicket orderTicket : orderTickets) {
//                    OrderTicketDto orderTicketDto = new OrderTicketDto(orderTicket);
//                    orderTicketList.add(orderTicketDto);
//                }
//            }
//        }

        
        System.out.println("totalPrice = " + totalPrice);
//
        model.addAttribute("orderTickets", orderTickets);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("orderNumList", orderNumList);

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