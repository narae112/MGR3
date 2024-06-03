package com.MGR.controller;

import com.MGR.constant.ReservationStatus;
import com.MGR.dto.OrderTicketDto;
import com.MGR.dto.ReservationDtlDto;
import com.MGR.entity.Member;
import com.MGR.entity.Order;
import com.MGR.entity.OrderTicket;
import com.MGR.repository.OrderRepository;
import com.MGR.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;

    // 결제 정보
    @GetMapping(value = {"/member/orders"})
    public String reservationList(@AuthenticationPrincipal PrincipalDetails member, Model model) {

        String email = member.getUsername();

        List<Order> orders = orderRepository.findOrders(email);
        // 로그인 한 멤버 주문 목록 조회

        List<OrderTicketDto> orderTicketList = new ArrayList<>();
        int totalPrice = 0; //합계 금액 구하려고
        List<String> orderNumLIst = new ArrayList<>(); //결제 후처리
        for (Order order : orders) {
            totalPrice += order.getTotalPrice();
            orderNumLIst.add(order.getOrderNum());
            if (order.getReservationStatus() == ReservationStatus.RESERVE) {
                List<OrderTicket> orderTickets = order.getOrderTickets();
                for (OrderTicket orderTicket : orderTickets) {
                    OrderTicketDto orderTicketDto = new OrderTicketDto(orderTicket);
                    orderTicketList.add(orderTicketDto);
                }
            }
        }
        System.out.println("orderNumLIst = " + orderNumLIst);
        System.out.println("totalPrice = " + totalPrice);

        model.addAttribute("orderTickets", orderTicketList);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("orderNumLIst", orderNumLIst);

        return "/checkout";
    }

}