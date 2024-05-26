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
                                   Errors errors, Model model,
                                   @AuthenticationPrincipal PrincipalDetails member,
                                   @RequestParam("eventImgFile") List<MultipartFile> imgFileList){
        if(errors.hasErrors()) {
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
                                   Errors errors, Model model, @PathVariable("id") Long id,
                                   @RequestParam("eventImgFile") List<MultipartFile> imgFileList){
        if(errors.hasErrors()) {
            return "/event/eventBoardForm";
        }

        try {
            eventBoardService.update(id, boardFormDto, imgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "티켓 수정 중 오류가 발생했습니다.");
            return "board/eventForm";
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

        List<Image> findImage = imageService.findByEvent(eventBoard);
        model.addAttribute("eventImage",findImage);
        System.out.println("findImage = " + findImage.get(0).getImgUrl().toString());

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
