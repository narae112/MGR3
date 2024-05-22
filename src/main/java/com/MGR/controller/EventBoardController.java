package com.MGR.controller;

import com.MGR.dto.EventBoardFormDto;
import com.MGR.entity.EventBoard;
import com.MGR.entity.Member;
import com.MGR.security.CustomUserDetails;
import com.MGR.service.EventBoardService;
import com.MGR.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class EventBoardController {

    private final MemberService memberService;
    private final EventBoardService eventBoardService;

    @GetMapping({"/events", "/events/{page}"})
    public String eventBoardList(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<EventBoard> paging = eventBoardService.getBoardList(page);
        model.addAttribute("paging", paging);

        return "board/event/eventBoardList";
    }

    @GetMapping("/eventBoard/new")
    public String eventBoardForm(Model model){
        //게시글 입력 폼
        model.addAttribute("eventBoardFormDto",new EventBoardFormDto());

        return "board/event/eventBoardForm";
    }

    @PostMapping("/eventBoard/create")
    public String eventBoardCreate(@Valid EventBoardFormDto BoardFormDto,
                                   Errors errors, Model model,
                                   @AuthenticationPrincipal CustomUserDetails member){
        //게시글 생성
        if(errors.hasErrors()) {
            return "board/event/eventBoardForm";
        }

        try {
            Member findMember = memberService.findById(member.getId()).orElseThrow();
            eventBoardService.saveBoard(BoardFormDto,findMember);
        } catch (IllegalStateException e){
            model.addAttribute("errors", e.getMessage());
            return "board/event/eventBoardForm";
        }
        return "redirect:/board/events";
    }

    @GetMapping("/event/{id}") //이벤트 게시판 게시글 id
    public String eventBoardDetail(@RequestParam("id") Long id){

//        eventBoardService.findById(id);


        return "event/eventBoardDtl/" + id;
    }

}
