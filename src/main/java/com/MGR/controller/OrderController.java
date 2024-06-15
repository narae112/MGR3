package com.MGR.controller;

import com.MGR.dto.*;
import com.MGR.entity.MemberCoupon;
import com.MGR.entity.Order;
import com.MGR.entity.OrderTicket;
import com.MGR.repository.MemberCouponRepository;
import com.MGR.repository.OrderRepository;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final OrderService orderService;

    // 결제 정보
    @GetMapping(value = {"/member/orders/{orderId}"})
    public String reservationList(@AuthenticationPrincipal PrincipalDetails member,
                                  @PathVariable("orderId") Long orderId, Model model) {

//        String email = member.getUsername();
//        List<Order> orders = orderRepository.findOrders(email);

        Order order = orderRepository.findById(orderId).get();
        List<OrderTicket> orderTicketList = orderRepository.findById(orderId).get().getOrderTickets();
        List<OrderTicketDto> orderTickets = new ArrayList<>();
        int totalPrice = orderRepository.findById(orderId).get().getAllTotalPrice();
        String orderNum = order.getOrderNum();

        System.out.println("orderNum1 = " + orderNum);

        List<String> orderNumList = new ArrayList<>(); //결제 후처리
        for (OrderTicket orderTicket : orderTicketList) {
            orderNumList.add(orderNum);
            System.out.println("orderNum2 = " + orderNum);
            OrderTicketDto dto = new OrderTicketDto(orderTicket);
            orderTickets.add(dto);
        }
        System.out.println(orderNumList.size());
        System.out.println("totalPrice = " + totalPrice);

        // 로그인한 멤버가 가진 쿠폰
        List<MemberCoupon> memberCouponList = memberCouponRepository.findAllByMemberId(member.getId());
        List<MemberCouponDto> memberCouponDtos = new ArrayList<>();

        for(MemberCoupon memberCoupon : memberCouponList) {
            if(!memberCoupon.isUsed()) {
                MemberCouponDto memberCouponDto = new MemberCouponDto(memberCoupon);
                memberCouponDtos.add(memberCouponDto);
                System.out.println("memberCouponDto = " + memberCouponDto.getName());
            }
        }

        model.addAttribute("orderTickets", orderTickets);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("orderNumList", orderNumList);
        model.addAttribute("memberCouponList", memberCouponDtos); // 쿠폰 리스트

        System.out.println("orderNumList = " + orderNumList);
        System.out.println("totalPrice = " + totalPrice);

        return "checkout";
    }

    // 결제 내역
    @GetMapping({"/member/orderList", "/member/orderList/{page}"})
    public String orderList(Model model, @AuthenticationPrincipal PrincipalDetails member,
                            @PathVariable(value = "page", required = false) Integer page) {

        if (member == null) {
            // 로그인 되어 있지 않은 경우 예외 처리
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        Long memberId = member.getId(); // 현재 인증된 사용자의 id

        // 페이지 번호가 없으면 기본값을 설정
        if (page == null || page < 0) {
            page = 0;
        }

        // 주문 목록을 가져와서 페이지화한 결과를 받아옴
        Page<OrderListDto> paging = orderService.getOrderList(page, memberId);
        model.addAttribute("paging", paging);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paging.getTotalPages());

        return "order/orderList";
    }
}