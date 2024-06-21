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

import java.util.*;

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
    public ResponseEntity<Map<String, String>> eventBoardCreate(@Valid EventBoardFormDto BoardFormDto,
                                                                BindingResult result, Model model,
                                                                @AuthenticationPrincipal PrincipalDetails member,
                                                                @RequestParam("eventImgFile") List<MultipartFile> imgFileList) {
        Map<String, String> errorResponse = new HashMap<>();

        // 각 필드별 오류 체크
        if (result.hasFieldErrors("title")) {
            errorResponse.put("field", "title");
            errorResponse.put("message", "제목을 입력하세요");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (result.hasFieldErrors("content")) {
            errorResponse.put("field", "content");
            errorResponse.put("message", "내용을 입력하세요");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (result.hasFieldErrors("startDate")) {
            errorResponse.put("field", "startDate");
            errorResponse.put("message", "이벤트 시작 날짜를 입력하세요");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (result.hasFieldErrors("endDate")) {
            errorResponse.put("field", "endDate");
            errorResponse.put("message", "이벤트 종료 날짜를 입력하세요");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // 이미지 파일이 없는 경우
        if (imgFileList.isEmpty() || imgFileList.get(0).isEmpty()) {
            errorResponse.put("field", "eventImgFile");
            errorResponse.put("message", "이벤트 이미지는 필수 입력 값입니다");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            Member findMember = memberService.findByEmail(member.getUsername()).orElseThrow();
            eventBoardService.saveBoard(BoardFormDto, findMember, imgFileList);
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "이벤트가 성공적으로 등록되었습니다");
            return ResponseEntity.ok().body(successResponse);
        } catch (IllegalStateException e) {
            errorResponse.put("message", "게시판 등록 중 오류가 발생했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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
    public ResponseEntity<Map<String, String>> updateEventBoard(@PathVariable Long id,
                                                                @Valid EventBoardFormDto BoardFormDto,
                                                                BindingResult result,
                                                                @AuthenticationPrincipal PrincipalDetails member,
                                                                @RequestParam(value = "eventImgFile", required = false) List<MultipartFile> imgFileList) {
        Map<String, String> errorResponse = new HashMap<>();

        // 각 필드별 오류 체크
        if (result.hasFieldErrors("title")) {
            errorResponse.put("field", "title");
            errorResponse.put("message", "제목을 입력하세요");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (result.hasFieldErrors("content")) {
            errorResponse.put("field", "content");
            errorResponse.put("message", "내용을 입력하세요");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (result.hasFieldErrors("startDate")) {
            errorResponse.put("field", "startDate");
            errorResponse.put("message", "이벤트 시작 날짜를 입력하세요");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (result.hasFieldErrors("endDate")) {
            errorResponse.put("field", "endDate");
            errorResponse.put("message", "이벤트 종료 날짜를 입력하세요");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // 이미지 파일이 없는 경우 (기존 이미지를 유지할 수 있으므로 체크하지 않습니다)
        if (imgFileList != null && imgFileList.get(0).isEmpty()) {
            errorResponse.put("field", "eventImgFile");
            errorResponse.put("message", "이벤트 이미지는 필수 입력 값입니다");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            Member findMember = memberService.findByEmail(member.getUsername()).orElseThrow();
            eventBoardService.update(id, BoardFormDto, imgFileList);  // 업데이트 로직 수행
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "이벤트가 성공적으로 수정되었습니다");
            return ResponseEntity.ok().body(successResponse);
        } catch (IllegalStateException e) {
            errorResponse.put("message", "게시판 수정 중 오류가 발생했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (Exception e) {
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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
