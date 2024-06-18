package com.MGR.controller;


import com.MGR.dto.MainTicketDto;
import com.MGR.dto.TicketFormDto;
import com.MGR.dto.TicketSearchDto;
import com.MGR.entity.Ticket;
import com.MGR.exception.DuplicateTicketNameException;
import com.MGR.service.FileService;
import com.MGR.service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping(value = "/admin/ticket/new")
    public String ticketForm(Model model) {
        model.addAttribute("ticketFormDto", new TicketFormDto());
        return "ticket/ticketForm";
    }

    @PostMapping("/admin/ticket/new")
    public ResponseEntity<String> ticketNew(@Valid TicketFormDto ticketFormDto, BindingResult bindingResult,
                                            Model model, @RequestParam("ticketImgFile") List<MultipartFile> ticketImgFileList) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("입력한 값들을 확인해주세요.");
        }
        // 이미지 파일이 없는 경우
        if (ticketImgFileList.isEmpty() || ticketImgFileList.get(0).isEmpty()) {
            return ResponseEntity.badRequest().body("상품 이미지는 필수 입력 값 입니다.");
        }
        try {
            ticketService.saveTicket(ticketFormDto, ticketImgFileList);
            return ResponseEntity.ok().body("티켓이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("티켓 등록 중 에러가 발생하였습니다.");
        }
    }

    @GetMapping(value = "/admin/ticket/{ticketId}")
    public String ticketDtl(@PathVariable("ticketId") Long ticketId, Model model, RedirectAttributes redirectAttributes) {
        try {
            TicketFormDto ticketFormDto = ticketService.getTicketDtl(ticketId);
            model.addAttribute("ticketFormDto", ticketFormDto);
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 티켓입니다.");
            model.addAttribute("ticketFormDto", new TicketFormDto());
            return "redirect:/admin/tickets";
        }
        return "ticket/ticketForm";
    }

    @PostMapping("/admin/ticket/{ticketId}")
    public ResponseEntity<String> ticketUpdateAjax(@PathVariable("ticketId") Long id,
                                                   @Valid TicketFormDto ticketFormDto,
                                                   BindingResult bindingResult,
                                                   @RequestParam("ticketImgFile") List<MultipartFile> ticketImgFileList) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("입력한 값들을 확인해주세요.");
        }
        // 이미지 파일이 없는 경우
        if (ticketImgFileList.isEmpty() || ticketImgFileList.get(0).isEmpty()) {
            return ResponseEntity.badRequest().body("상품 이미지는 필수 입력 값 입니다.");
        }

        try {
            ticketService.updateTicket(id, ticketFormDto, ticketImgFileList);
            return ResponseEntity.ok().body("티켓이 성공적으로 수정되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("이미지 ID 목록과 파일 목록의 크기가 다릅니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("티켓 수정 중 오류가 발생했습니다. 상세 오류: " + e.getMessage());
        }
    }
    @GetMapping(value = {"/admin/tickets", "/admin/tickets/{page}"})
    public String ticketManage(TicketSearchDto ticketSearchDto,
                               @PathVariable("page") Optional<Integer> page, Model model){
        int pageNumber = page.orElse(0); // 페이지 매개변수가 없는 경우 0으로 초기화
        Pageable pageable = PageRequest.of(pageNumber, 5);

        Page<Ticket> tickets = ticketService.getAdminTicketPage(ticketSearchDto, pageable);

        model.addAttribute("tickets",tickets);
        model.addAttribute("ticketSearchDto", ticketSearchDto);
        model.addAttribute("pageNumber", tickets.getNumber());
        return "ticket/ticketMng";
    }

    @GetMapping(value="/ticket/{ticketId}")
    public String ticketDtl(Model model, @PathVariable("ticketId") Long ticketId){
        TicketFormDto ticketFormDto = ticketService.getTicketDtl(ticketId);
        model.addAttribute("ticket", ticketFormDto);

        return "ticket/ticketDtl";
    }
    @GetMapping(value={"tickets", "/tickets/{page}"})
    public String ticketMain(TicketSearchDto ticketSearchDto,
                             @PathVariable Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.orElse(0), 3); // 페이지 번호를 받아오는 부분 수정
        Page<MainTicketDto> tickets = ticketService.getMainTicketPage(ticketSearchDto, pageable);

        model.addAttribute("tickets", tickets);
        model.addAttribute("ticketSearchDto", ticketSearchDto);
        model.addAttribute("maxPage", 3);

        return "ticket/ticketMain";
    }
    @GetMapping("/admin/ticket/delete/{ticketId}")
    public String deleteTicket(@PathVariable("ticketId") Long ticketId, RedirectAttributes redirectAttributes) {
        try {
            // 티켓 삭제
            ticketService.deleteTicket(ticketId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "예약된 티켓은 삭제할 수 없습니다.");
            return "redirect:/admin/tickets";
        }

        return "redirect:/admin/tickets";
    }






}
