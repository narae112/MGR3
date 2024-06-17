package com.MGR.controller;

import com.MGR.dto.OrderListDto;
import com.MGR.entity.Member;
import com.MGR.entity.Order;
import com.MGR.service.OrderService;
import com.MGR.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
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
        for (OrderListDto orderDto : paging) {
            Order order = orderService.findOrderByOrderNum(orderDto.getOrderNum());
            Member member = order.getMember();
            memberMap.put(orderDto.getOrderNum(), member);
        }

        model.addAttribute("paging", paging);
        model.addAttribute("memberMap", memberMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paging.getTotalPages());

        return "order/paymentList";
    }
}

