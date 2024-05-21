package com.MGR.controller;

import com.MGR.dto.EventBoardFormDto;
import com.MGR.entity.Member;
import com.MGR.repository.EventBoardRepository;
import com.MGR.security.CustomUserDetails;
import com.MGR.service.EventBoardService;
import com.MGR.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class EventBoardController {

    private final MemberService memberService;
    private final EventBoardService eventBoardService;

    @GetMapping("/event")
    public String eventBoardList(){
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
        return "board/event/eventBoardList";
    }

}
