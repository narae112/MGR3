package com.MGR.service;

import com.MGR.constant.EventType;
import com.MGR.dto.EventBoardFormDto;
import com.MGR.entity.Attraction;
import com.MGR.entity.EventBoard;
import com.MGR.entity.Image;
import com.MGR.entity.Member;
import com.MGR.repository.EventBoardRepository;
import com.MGR.repository.ImageRepository;
import com.MGR.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventBoardService {

    private final EventBoardRepository eventBoardRepository;
    private final ImageService imageService;
    private final NotificationService notificationService;


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

        notificationService.notifyBoard(board);
    }

    public Page<EventBoard> getBoardList(int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));

        // 먼저 페이지 정보를 불러옴
        Page<EventBoard> boards = eventBoardRepository.findAll(pageable);

        // 오늘 날짜를 구함
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

        // `endDate`가 오늘 날짜를 지나지 않은 것을 오름차순으로 정렬하고, 오늘 날짜 이전인 것을 마지막에 위치하도록 정렬
        List<EventBoard> sortedBoards = boards.stream()
                .sorted((b1, b2) -> {
                    LocalDate endDate1 = LocalDate.parse(b1.getEndDate(), formatter);
                    LocalDate endDate2 = LocalDate.parse(b2.getEndDate(), formatter);
                    boolean isEndDate1Past = endDate1.isBefore(today);
                    boolean isEndDate2Past = endDate2.isBefore(today);

                    if (isEndDate1Past && !isEndDate2Past) {
                        return 1;
                    } else if (!isEndDate1Past && isEndDate2Past) {
                        return -1;
                    } else if (!isEndDate1Past && !isEndDate2Past) {
                        return endDate1.compareTo(endDate2);
                    } else {
                        return endDate2.compareTo(endDate1);
                        // 둘 다 과거이면 내림차순
                    }
                })
                .collect(Collectors.toList());

        return new PageImpl<>(sortedBoards, pageable, boards.getTotalElements());
    }

    public void saveBoardCount(EventBoard board) {
        eventBoardRepository.save(board);
    }

    public List<EventBoard> findAll() {
        return  eventBoardRepository.findAll();
    }

    public Optional<EventBoard> findById(Long id) {
        Optional<EventBoard> eventBoard = eventBoardRepository.findById(id);
        return eventBoard;
    }

    public void delete(EventBoard eventBoard) {
        imageService.deleteImage(eventBoard);
        eventBoardRepository.delete(eventBoard);
    }

    public EventBoard update(Long id, EventBoardFormDto boardFormDto, List<MultipartFile> imgFileList) throws Exception {
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

    public List<EventBoard> findAllCurrentEvents() {
        List<EventBoard> eventBoardList = eventBoardRepository.findAll();
        List<EventBoard> currentEventList = new ArrayList<>();
        for (EventBoard eventBoard : eventBoardList) {
            if (eventBoard.getIsEventCurrent()) {
                currentEventList.add(eventBoard);
            }
        }
        return currentEventList;
    }

    @Bean
    public CommandLineRunner initEventBoard(PasswordEncoder passwordEncoder, EventBoardRepository eventBoardRepository, MemberRepository memberRepository) {
        return args -> {

            Member member = memberRepository.findByRole("ROLE_ADMIN");

            if (member != null) {

                List<EventBoard> eventBoards = List.of(
                        new EventBoard(EventType.EVENT, "현대M포인트 프로모션 ", " ", "2024-06-01", "2024-06-30", member),
                        new EventBoard(EventType.EVENT, "슬기로운 MGR 생활", " ", "2024-06-01", "2024-07-31", member),
                        new EventBoard(EventType.EVENT, "슬기로운 네이버페이 생활(온라인/모바일 예매)", " ", "2024-06-01", "2024-08-31", member),
                        new EventBoard(EventType.EVENT, "조선시대 교복체험 패키지", " ", "2024-06-01", "2024-09-30", member),
                        new EventBoard(EventType.EVENT, "혜택은 KT가 준비할게, MGR는 누가 올래?", " ", "2024-06-01", "2024-10-31", member),
                        new EventBoard(EventType.EVENT, "케이뱅크 여름나들이 이벤트", " ", "2024-06-01", "2024-11-30", member),
                        new EventBoard(EventType.NOTICE, "주차시스템 개선 안내", " ", "2024-06-01", "2024-12-31", member)
                );

                for (int i = 0; i < eventBoards.size(); i++) {
                    EventBoard eventBoard = eventBoards.get(i);

                    if (!eventBoardRepository.findByTitle(eventBoard.getTitle()).isPresent()) {
                        eventBoardRepository.save(eventBoard);

                        Image image = new Image();
                        image.setEventBoard(eventBoard);
                        if (i == 0) {
                            image.setImgOriName("event" + (i + 1) + ".png");
                            image.setImgUrl("/images/board/event" + (i + 1) + ".png");
                        } else {
                            image.setImgOriName("event" + (i + 1) + ".jpg");
                            image.setImgUrl("/images/board/event" + (i + 1) + ".jpg");
                        }
                        imageService.save(image);
                    }
                }
            }
        };
    }
}
