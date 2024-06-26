package com.MGR.controller;

import com.MGR.dto.ReservationDtlDto;
import com.MGR.dto.ReservationOrderDto;
import com.MGR.dto.ReservationTicketDto;
import com.MGR.entity.ReservationTicket;
import com.MGR.exception.InsufficientInventoryException;
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
    @GetMapping({"/reservations", "/reservations/{page}"})
    public String reservationList(Model model,
                                  @PathVariable(value = "page", required = false) Integer page,
                                  @AuthenticationPrincipal PrincipalDetails member){

        if (page == null) {
            page = 0; // 페이지 값이 없을 경우 기본값을 0으로 설정
        }

        Page<ReservationDtlDto> paging = reservationService.getReservationList(member.getUsername(), PageRequest.of(page, 4));
        model.addAttribute("paging", paging);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paging.getTotalPages());

        return "reservation/reservationList";
    }

    // 티켓 수량 수정
    @PatchMapping("/reservationTicket/{reservationTicketId}")
    public @ResponseBody ResponseEntity<?> updateReserveTicket(@PathVariable("reservationTicketId") Long reservationTicketId,
                                                               @RequestBody ReservationTicket updateRequest,
                                                               @AuthenticationPrincipal PrincipalDetails member) {
        System.out.println("reservationTicketId = " + reservationTicketId);
        System.out.println("adultCount = " + updateRequest.getAdultCount());
        System.out.println("childCount = " + updateRequest.getChildCount());

        Integer adultCount = updateRequest.getAdultCount();
        Integer childCount = updateRequest.getChildCount();

        if (adultCount == null || adultCount < 1 || childCount == null || childCount < 0) {
            // 조건을 만족하지 않으면 요청을 처리하지 않고 BadRequest를 반환
            return new ResponseEntity<String>("입력 값이 올바르지 않습니다", HttpStatus.BAD_REQUEST);
        }

        if (!reservationService.validateReserveTicket(reservationTicketId, member.getUsername())) {
            return new ResponseEntity<String>("수정 권한이 없습니다", HttpStatus.FORBIDDEN);
        }

        try {
            reservationService.updateReservationTicketCount(reservationTicketId, adultCount, childCount);
        } catch (InsufficientInventoryException e) {
            // 재고 부족 예외 처리
            return new ResponseEntity<String>("재고가 부족합니다", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(reservationTicketId, HttpStatus.OK);
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
