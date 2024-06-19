package com.MGR.controller;

import com.MGR.dto.OrderListDto;
import com.MGR.entity.Member;
import com.MGR.entity.Order;
import com.MGR.entity.OrderTicket;
import com.MGR.repository.OrderTicketRepository;
import com.MGR.service.OrderService;
import com.MGR.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;

    @GetMapping({"/admin/paymentList", "/admin/paymentList/{page}"})
    public String paymentList(Model model, @PathVariable(value = "page", required = false) Integer page) {
        if (page == null || page < 0) {
            page = 0;
        }

        Page<OrderListDto> paging = orderService.getAllOrderList(page);
        Map<String, Member> memberMap = new HashMap<>();
        Map<String, List<OrderTicket>> orderTicketMap = new HashMap<>();

        for (OrderListDto orderDto : paging) {
            Order order = orderService.findOrderByOrderNum(orderDto.getOrderNum());
            Member member = order.getMember();
            memberMap.put(orderDto.getOrderNum(), member);
            List<OrderTicket> orderTicketList = orderService.findOrderTicketByOrderId(order.getId());
            orderTicketMap.put(orderDto.getOrderNum(), orderTicketList);
        }

        model.addAttribute("paging", paging);
        model.addAttribute("orderTicketMap", orderTicketMap);
        model.addAttribute("memberMap", memberMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paging.getTotalPages());

        return "order/paymentList";
    }


    @GetMapping({"/admin/paymentGraph", "/admin/paymentGraph/{page}"})
    public String paymentGraph(Model model, @PathVariable(value = "page", required = false) Integer page) {
        if (page == null || page < 0) {
            page = 0;
        }

        Page<OrderListDto> paging = orderService.getAllOrderList(page);
        Map<String, Member> memberMap = new HashMap<>();

        // 각 주문에 대한 멤버 정보 가져오기
        for (OrderListDto orderDto : paging.getContent()) {
            Order order = orderService.findOrderByOrderNum(orderDto.getOrderNum());
            if (order != null) {
                Member member = order.getMember();
                memberMap.put(orderDto.getOrderNum(), member);
            }
        }

        // 날짜별 총 결제 금액 가져오기
        Map<LocalDate, Integer> totalPriceByDate = orderService.getTotalPriceByDate(paging.getContent());

        model.addAttribute("paging", paging);
        model.addAttribute("memberMap", memberMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paging.getTotalPages());
        model.addAttribute("totalPriceByDate", totalPriceByDate);
        return "order/paymentGraph";
    }
}

