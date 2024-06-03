package com.MGR.controller;

import com.MGR.dto.ReservationDtlDto;
import com.MGR.dto.ReservationOrderDto;
import com.MGR.dto.ReservationTicketDto;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class ReservationController {
    private final ReservationService reservationService;

    // 예약하기
    @PostMapping("/reservation")
    public @ResponseBody ResponseEntity reservation(@RequestBody @Valid ReservationTicketDto reservationTicketDto,
                                                    BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails member) {
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = member.getUsername(); // 로그인한 멤버 이메일
        Long reservationTicketId;

        try {
            reservationTicketId = reservationService.addReservation(reservationTicketDto, email); // 받아온 요청 & 이메일 넘겨서 reservationTicketId 가져오기
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(reservationTicketId, HttpStatus.OK);
    }

    // 예약 내역 보기
    @GetMapping(value = {"/reservations", "/reservations/{page}"})
    public String reservationList(@PathVariable("page") Optional<Integer> page, @AuthenticationPrincipal PrincipalDetails member, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
        Page<ReservationDtlDto> reservationDtlList = reservationService.getReservationList(member.getUsername(), pageable);

        model.addAttribute("reservationTickets", reservationDtlList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "reservation/reservationList";
    }

    // 티켓 수량 수정
    @PatchMapping("/reservationTicket/{reservationTicketId}")
    public @ResponseBody ResponseEntity updateReserveTicket(@PathVariable("reservationTicketId") Long reservationTicketId, int adultCount, int childCount, @AuthenticationPrincipal PrincipalDetails member) {
        if(adultCount <= 0) {
            return new ResponseEntity<String>("성인 티켓 수량은 1개 미만이 될 수 없습니다, 아동은 반드시 성인 보호자를 동반해야 합니다", HttpStatus.BAD_REQUEST);
        } else if(!reservationService.validateReserveTicket(reservationTicketId, member.getUsername())) {
            return new ResponseEntity<String>("수정 권한이 없습니다", HttpStatus.FORBIDDEN);
        }
        reservationService.updateReservationTicketCount(reservationTicketId, adultCount, childCount);

        return new ResponseEntity<Long>(reservationTicketId, HttpStatus.OK);
    }

    // 예약 취소
    @PostMapping(value = "/reservationTicket/{reservationTicketId}/cancel")
    public @ResponseBody ResponseEntity cancelReserveTicket(@PathVariable("reservationTicketId") Long reservationTicketId,
                                                            @AuthenticationPrincipal PrincipalDetails member){
        if(!reservationService.validateReserveTicket(reservationTicketId, member.getUsername())){
            return new ResponseEntity<String>("예약 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        reservationService.cancelReservation(reservationTicketId);
        return new ResponseEntity<Long>(reservationTicketId, HttpStatus.OK);
    }

    // 결제할 티켓 정보
    @PostMapping("/reservation/orders")
    public @ResponseBody ResponseEntity orderReservationTicket(@RequestBody ReservationOrderDto reservationOrderDto,
                                                               @AuthenticationPrincipal PrincipalDetails member) {
        List<ReservationOrderDto> reservationOrderDtoList = reservationOrderDto.getReservationOrderDtoList();
        String email = member.getUsername();

        if(reservationOrderDtoList == null || reservationOrderDtoList.size() == 0) {
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        for(ReservationOrderDto reservationOrder : reservationOrderDtoList) {
            if(!reservationService.validateReserveTicket(reservationOrder.getReservationTicketId(), email)) {
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        Long orderId = reservationService.orderReservationTicket(reservationOrderDtoList, email);

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }




}