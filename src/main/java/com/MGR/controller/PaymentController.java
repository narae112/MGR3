package com.MGR.controller;

import com.MGR.dto.OrderListDto;
import com.MGR.dto.OrderSearchDto;
import com.MGR.entity.Member;
import com.MGR.entity.Order;
import com.MGR.entity.OrderTicket;
import com.MGR.repository.OrderTicketRepository;
import com.MGR.service.OrderService;
import com.MGR.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Map<String, Integer> orderTicketAdultCountMap = new HashMap<>();
        Map<String, Integer> orderTicketChildCountMap = new HashMap<>();

        for (OrderListDto orderDto : paging) {
            Order order = orderService.findOrderByOrderNum(orderDto.getOrderNum());
            Member member = order.getMember();
            memberMap.put(orderDto.getOrderNum(), member);
            List<OrderTicket> orderTicketList = orderService.findOrderTicketByOrderId(order.getId());
            orderTicketMap.put(orderDto.getOrderNum(), orderTicketList);

            int adultCountSum = orderTicketList.stream().mapToInt(OrderTicket::getAdultCount).sum();
            int childCountSum = orderTicketList.stream().mapToInt(OrderTicket::getChildCount).sum();
            orderTicketAdultCountMap.put(orderDto.getOrderNum(), adultCountSum);
            orderTicketChildCountMap.put(orderDto.getOrderNum(), childCountSum);
        }

        model.addAttribute("paging", paging);
        model.addAttribute("orderTicketMap", orderTicketMap);
        model.addAttribute("memberMap", memberMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paging.getTotalPages());
        model.addAttribute("orderTicketAdultCountMap", orderTicketAdultCountMap);
        model.addAttribute("orderTicketChildCountMap", orderTicketChildCountMap);

        return "order/paymentList";
    }


   @GetMapping({"/admin/paymentGraph", "/admin/paymentGraph/{page}"})
    public String paymentGraph(OrderSearchDto orderSearchDto, Model model, @PathVariable(value = "page", required = false) Integer page) {

        if (page == null || page < 0) {
            page = 0;
        }

        Pageable pageable = PageRequest.of(page, 365, Sort.by("orderDate").descending());
        Page<OrderListDto> paging = orderService.getAllOrderGraph(orderSearchDto, pageable);

        LocalDate startDate = orderSearchDto.getStartDate();
        LocalDate endDate = orderSearchDto.getEndDate();

        Map<LocalDate, Integer> totalPriceByDate = orderService.getTotalPriceByDate(paging.getContent());
//      Map<LocalDate, Integer> totalCountByDate = orderService.getTotalCountByDate(paging.getContent());
      Map<LocalDate, Integer> childCountByDate = orderService.getChildCountByDate(paging.getContent());
      Map<LocalDate, Integer> adultCountByDate = orderService.getAdultCountByDate(paging.getContent());

        if (startDate != null && endDate != null) {
            totalPriceByDate = totalPriceByDate.entrySet().stream()
                    .filter(entry -> !entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            childCountByDate = childCountByDate.entrySet().stream()
                 .filter(entry -> !entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            adultCountByDate = adultCountByDate.entrySet().stream()
                    .filter(entry -> !entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        model.addAttribute("childCountByDate", childCountByDate);
        model.addAttribute("adultCountByDate", adultCountByDate);
        model.addAttribute("orderSearchDto", orderSearchDto);
        model.addAttribute("paging", paging);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paging.getTotalPages());
        model.addAttribute("totalPriceByDate", totalPriceByDate);

        // 디버깅 로그 추가
        System.out.println("Total Price By Date: " + totalPriceByDate);
        System.out.println("Total Price By Date: " + adultCountByDate);
        System.out.println("Total Price By Date: " + childCountByDate);

        return "order/paymentGraph";
    }
}

