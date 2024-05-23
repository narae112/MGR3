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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

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
        model.addAttribute("eventBoardFormDto",new EventBoard());

        return "board/event/eventBoardForm";
    }

    @PostMapping("/eventBoard/create")
    public String eventBoardCreate(@Valid EventBoardFormDto BoardFormDto,
                                   Errors errors, Model model,
                                   @AuthenticationPrincipal CustomUserDetails member){
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


    @GetMapping("/eventBoard/edit/{id}")
    public String editEventBoard(@PathVariable("id") Long id, Model model,
                                 @AuthenticationPrincipal CustomUserDetails member){

        EventBoard eventBoard = eventBoardService.findById(id).orElseThrow();
//        String authorEmail = eventBoard.getMember().getEmail(); //작성자의 이메일 구하기
//        String userEmail = member.getUsername(); //로그인 되어있는 사용자의 이메일 구하기
//
//        if(!userEmail.equals(authorEmail)){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "작성자만 수정할 수 있습니다.");
//        }

        model.addAttribute("eventBoardFormDto",eventBoard);

        return "board/event/eventBoardForm";
    }

    @PostMapping("/eventBoard/update/{id}")
    public String UpdateEventBoard(@Valid EventBoardFormDto boardFormDto,
                                   Errors errors, Model model,
                                   @PathVariable("id") Long id){
        if(errors.hasErrors()) {
            return "board/event/eventBoardForm";
        }

        EventBoard update = eventBoardService.update(id, boardFormDto);
        model.addAttribute("eventBoardFormDto",update);

        return "redirect:/board/event/" + id;
    }

    @GetMapping("/event/{id}") //이벤트 게시판 게시글 id
    public String eventBoardDetail(@PathVariable("id") Long id,Model model){

        EventBoard eventBoard = eventBoardService.findById(id).orElseThrow();
        int count = eventBoard.viewCount();
        eventBoard.setCount(count);
        eventBoardService.saveBoard(eventBoard);

        model.addAttribute("eventBoard",eventBoard);

        return "board/event/eventBoardDtl";
    }

    @PostMapping("/event/delete/{id}") //게시글 삭제
    @ResponseBody
    public String eventBoardDelete(@PathVariable("id") Long id){

        Optional<EventBoard> findBoard = eventBoardService.findById(id);
        if(findBoard.isPresent()){
            EventBoard eventBoard = findBoard.get();
            eventBoardService.delete(eventBoard);
            return "게시글이 삭제되었습니다";
        }
        return "작성자가 아니면 삭제가 불가능합니다.";
    }

}
