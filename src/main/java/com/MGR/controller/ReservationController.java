//package com.MGR.controller;
//
//import com.MGR.dto.ReservationDtlDto;
//import com.MGR.dto.ReservationTicketDto;
//import com.MGR.service.ReservationService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.annotation.*;
//
//import java.security.Principal;
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/member")
//public class ReservationController {
//    private final ReservationService reservationService;
//
//    // 예약하기
//    @PostMapping("/reservation")
//    public @ResponseBody ResponseEntity reservation(@RequestBody @Valid ReservationTicketDto reservationTicketDto,
//                                              BindingResult bindingResult, Principal principal) {
//        if(bindingResult.hasErrors()){
//            StringBuilder sb = new StringBuilder();
//            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//
//            for (FieldError fieldError : fieldErrors) {
//                sb.append(fieldError.getDefaultMessage());
//            }
//
//            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
//        }
//
//        String email = principal.getName();
//        Long reservationTicketId;
//
//        try {
//            reservationTicketId = reservationService.addReservation(reservationTicketDto, email);
//        } catch (Exception e) {
//            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//        return new ResponseEntity<Long>(reservationTicketId, HttpStatus.OK);
//    }
//
//    @GetMapping("/reservation")
//    public String reservationList(Principal principal, Model model) {
//        List<ReservationDtlDto> reservationDtlList = reservationService.getReservationList(principal.getName());
//        model.addAttribute("reservationTickets", reservationDtlList);
//
//        return "reservation/reservationList";
//    }
//
//    // 티켓 수량 수정
//    @PatchMapping("/reservationTicket/{reservationTicketId}")
//    public @ResponseBody ResponseEntity updateReserveTicket(@PathVariable("reservationTicketId") Long reservationTicketId, int ticketCount, Principal principal) {
//        if(ticketCount <= 0) {
//            return new ResponseEntity<String>("티켓 수량은 1개 미만이 될 수 없습니다", HttpStatus.BAD_REQUEST);
//        } else if(!reservationService.validateReserveTicket(reservationTicketId, principal.getName())) {
//            return new ResponseEntity<String>("수정 권한이 없습니다", HttpStatus.FORBIDDEN);
//        }
//        reservationService.updateReservationTicketCount(reservationTicketId, ticketCount);
//
//        return new ResponseEntity<Long>(reservationTicketId, HttpStatus.OK);
//    }
//
//    // 예약 취소
//    @PostMapping(value = "/reservationTicket/{reservationTicketId}/cancel")
//    public @ResponseBody ResponseEntity cancelReserveTicket(@PathVariable("reservationTicketId") Long reservationTicketId,
//                                                       Principal principal){
//        if(!reservationService.validateReserveTicket(reservationTicketId, principal.getName())){
//            return new ResponseEntity<String>("예약 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
//        }
//        reservationService.cancelReservation(reservationTicketId);
//        return new ResponseEntity<Long>(reservationTicketId, HttpStatus.OK);
//    }
//
//
//}