package com.MGR.controller;

import com.MGR.dto.EventBoardFormDto;
import com.MGR.entity.EventBoard;
import com.MGR.entity.Image;
import com.MGR.entity.Member;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.EventBoardService;
import com.MGR.service.ImageService;
import com.MGR.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class EventBoardController {

    private final MemberService memberService;
    private final EventBoardService eventBoardService;
    private final ImageService imageService;

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
                                   BindingResult result, Model model,
                                   @AuthenticationPrincipal PrincipalDetails member,
                                   @RequestParam("eventImgFile") List<MultipartFile> imgFileList){
        if(result.hasErrors()) {
            model.addAttribute("BoardFormDto", BoardFormDto);
            return "board/event/eventBoardForm";
        }
        try {
            Member findMember = memberService.findByEmail(member.getUsername()).orElseThrow();
            eventBoardService.saveBoard(BoardFormDto,findMember, imgFileList);
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage","게시판 등록 중 오류가 발생했습니다");
            return "board/event/eventBoardForm";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/board/events";
    }

    @GetMapping("/eventBoard/edit/{id}")
    public String editEventBoard(@PathVariable("id") Long id, Model model,
                                 @AuthenticationPrincipal PrincipalDetails member){

        EventBoard eventBoard = eventBoardService.findById(id).orElseThrow();
        model.addAttribute("eventBoardFormDto",eventBoard);

        Image findImage = imageService.findByEvent(eventBoard);
        model.addAttribute("eventImage",findImage);

        return "board/event/eventBoardForm";
    }

    @PostMapping("/eventBoard/update/{id}")
    public String UpdateEventBoard(@Valid EventBoard eventBoard,
                                   BindingResult result, Model model, @PathVariable("id") Long id,
                                   @RequestParam(value = "eventImgFile", required = false) List<MultipartFile> imgFileList){
        if(result.hasErrors()) {
            return "board/event/eventBoardForm";
        }

        try {
            eventBoardService.update(id, eventBoard, imgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "수정 중 오류가 발생했습니다.");
            return "board/event/eventBoardForm";
        }

        return "redirect:/board/event/" + id;
    }

    @GetMapping("/event/{id}") //이벤트 게시판 게시글 id
    public String eventBoardDetail(@PathVariable("id") Long id,Model model){

        EventBoard eventBoard = eventBoardService.findById(id).orElseThrow();
        int count = eventBoard.viewCount();
        eventBoard.setCount(count);
        eventBoardService.saveBoard(eventBoard);
        model.addAttribute("eventBoard",eventBoard);

        Image findImage = imageService.findByEvent(eventBoard);
        model.addAttribute("eventImage",findImage);

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
        return "삭제 오류";
    }

}
