package com.MGR.service;

import com.MGR.dto.EventBoardFormDto;
import com.MGR.dto.ImageDto;
import com.MGR.entity.EventBoard;
import com.MGR.entity.Image;
import com.MGR.entity.Member;
import com.MGR.entity.Ticket;
import com.MGR.repository.EventBoardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventBoardService {

    private final EventBoardRepository eventBoardRepository;
    private final ImageService imageService;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")  // 이벤트가 진행중인 게시글의 진행여부를 매일 자정에 업데이트
    public void updateEventStatuses() {
        LocalDate now = LocalDate.now();
        List<EventBoard> eventBoards = eventBoardRepository.findAll();
        //자정이 되면 이벤트 게시판을 모두 불러냄

        for (EventBoard eventBoard : eventBoards) {
            //이벤트 진행 여부를 다시 설정함
            boolean isCurrent = (now.isEqual(LocalDate.parse(eventBoard.getStartDate())) || now.isAfter(LocalDate.parse(eventBoard.getStartDate())))
                    && (now.isEqual(LocalDate.parse(eventBoard.getEndDate())) || now.isBefore(LocalDate.parse(eventBoard.getEndDate())));
            eventBoard.setIsEventCurrent(isCurrent);
        }
    }

    public void saveBoard(EventBoardFormDto boardFormDto, Member member, List<MultipartFile> imgFileList) throws Exception {
        EventBoard board = EventBoard.createBoard(boardFormDto, member);
        eventBoardRepository.save(board);

        //이미지 등록
        for(int i =0; i< imgFileList.size(); i++){
            Image boardImage = new Image();
            boardImage.setEventBoard(board);

            if(i == 0){
                boardImage.setRepImgYn(true);
            }else{
                boardImage.setRepImgYn(false);
            }
            imageService.saveBoardImage(boardImage, imgFileList.get(i));
        }

    }

    public void saveBoard(EventBoard board) {
        eventBoardRepository.save(board);
    }

    public List<EventBoard> findAll() {
        return  eventBoardRepository.findAll();
    }

    public Page<EventBoard> getBoardList(int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));

        return eventBoardRepository.findAll(pageable);
    }

    public Optional<EventBoard> findById(Long id) {
        Optional<EventBoard> eventBoard = eventBoardRepository.findById(id);
        return eventBoard;
    }

    public void delete(EventBoard eventBoard) {
        imageService.deleteImage(eventBoard);
        eventBoardRepository.delete(eventBoard);
    }

//    public EventBoard update(Long id, EventBoardFormDto boardFormDto) {
//        EventBoard eventBoard = eventBoardRepository.findById(id).orElseThrow();
//        eventBoard.setContent(boardFormDto.getContent());
//        eventBoard.setTitle(boardFormDto.getTitle());
//        eventBoard.setStartDate(boardFormDto.getStartDate());
//        eventBoard.setEndDate(boardFormDto.getEndDate());
//        eventBoard.setModifiedDate(LocalDateTime.now());
////        eventBoard.setType(boardFormDto.getType());
//        return eventBoard;
//    }

    public EventBoard update(Long id, EventBoard boardFormDto, List<MultipartFile> imgFileList) throws Exception {
        EventBoard eventBoard = eventBoardRepository.findById(id).orElseThrow();
        eventBoard.setContent(boardFormDto.getContent());
        eventBoard.setTitle(boardFormDto.getTitle());
        eventBoard.setStartDate(boardFormDto.getStartDate());
        eventBoard.setEndDate(boardFormDto.getEndDate());
        eventBoard.setModifiedDate(LocalDateTime.now());
        eventBoardRepository.save(eventBoard);

        Image findImage = imageService.findByEvent(eventBoard);

        MultipartFile imgFile = imgFileList.get(0);

        imageService.saveBoardImage(findImage,imgFile);

        return eventBoard;
    }
}
