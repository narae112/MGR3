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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @PostMapping("/eventBoard/new")
    public ResponseEntity<String> eventBoardCreate(@Valid EventBoardFormDto BoardFormDto,
                                                   BindingResult result, Model model,
                                                   @AuthenticationPrincipal PrincipalDetails member,
                                                   @RequestParam("eventImgFile") List<MultipartFile> imgFileList) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("입력한 값들을 확인해주세요.");
        }
        try {
            Member findMember = memberService.findByEmail(member.getUsername()).orElseThrow();
            eventBoardService.saveBoard(BoardFormDto, findMember, imgFileList);
            return ResponseEntity.ok("이벤트가 성공적으로 등록되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시판 등록 중 오류가 발생했습니다");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
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

    @PostMapping("/eventBoard/edit/{id}")
    public ResponseEntity<String> updateEventBoard(@PathVariable Long id,
                                                   @Valid EventBoard eventBoard,
                                                   BindingResult result,
                                                   @RequestParam(value = "eventImgFile", required = false) List<MultipartFile> imgFileList) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("입력한 값들을 확인해주세요.");
        }

        try {
            eventBoardService.update(id, eventBoard, imgFileList);  // 업데이트 로직 수행
            return ResponseEntity.ok().body("이벤트가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 중 오류가 발생했습니다.");
        }
    }
    @GetMapping("/event/{id}") //이벤트 게시판 게시글 id
    public String eventBoardDetail(@PathVariable("id") Long id,Model model){

        EventBoard eventBoard = eventBoardService.findById(id).orElseThrow();
        int count = eventBoard.viewCount();
        eventBoard.setCount(count);
        eventBoardService.saveBoardCount(eventBoard);
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
