package com.MGR.service;

import com.MGR.constant.LocationCategory;
import com.MGR.dto.ImageDto;
import com.MGR.dto.ReviewBoardForm;
import com.MGR.entity.*;
import com.MGR.exception.DataNotFoundException;
import com.MGR.repository.ImageRepository;
import com.MGR.repository.ReviewBoardRepository;
import com.MGR.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewBoardService {

    private final ReviewBoardRepository reviewBoardRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final NotificationService notificationService;


    @SuppressWarnings("unused")
    private Specification<ReviewBoard> search(String kw){
        return new Specification<>(){
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<ReviewBoard> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<ReviewBoard, Member> u1 = q.join("author", JoinType.LEFT);
                Join<ReviewBoard, ReviewComment> a = q.join("commentList", JoinType.LEFT);
                Join<ReviewComment, Member> u2 = a.join("author", JoinType.LEFT);
                return cb.or(
                        cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"), // 내용
                        cb.like(u1.get("nickname"), "%" + kw + "%"), // 질문 작성자의 닉네임
                        cb.like(a.get("content"), "%" + kw + "%"), // 답변 내용
                        cb.like(u2.get("nickname"), "%" + kw + "%")
                );
            }
        };
    }


    public ReviewBoard getReviewBoard(Long id){
        Optional<ReviewBoard> reviewBoard = this.reviewBoardRepository.findById(id);
        if(reviewBoard.isPresent()){
            return reviewBoard.get();

        }else{
            throw new DataNotFoundException("review not found");
        }
    }

    @Transactional(readOnly = true)
    public List<ReviewBoardForm> getReviewBoardForms() {
        List<ReviewBoard> reviewBoards = reviewBoardRepository.findAll();
        return reviewBoards.stream().map(this::convertToForm).collect(Collectors.toList());
    }
    public Long createReviewBoard(String subject, String content, Member user, List<MultipartFile> reviewImgFileList) throws Exception {
        ReviewBoard q = new ReviewBoard();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.reviewBoardRepository.save(q);

        for (int i = 0; i < reviewImgFileList.size(); i++) {
            Image reviewImage = new Image();
            reviewImage.setReviewBoard(q);

            if (i == 0) {
                reviewImage.setRepImgYn(true);
            } else {
                reviewImage.setRepImgYn(false);
            }
            imageService.saveReviewImage(reviewImage, reviewImgFileList.get(i));
        }
        return q.getId();
    }


    public void delete(ReviewBoard reviewBoard) throws Exception {
        try {
            imageService.deleteImagesByReviewBoardId(reviewBoard.getId());
            this.reviewBoardRepository.delete(reviewBoard);
        } catch (Exception e) {
            throw new Exception("질문 삭제 중 오류가 발생하였습니다.", e);
        }
    }

    @Transactional
    public void modify(ReviewBoard reviewBoard, String subject, String content, List<MultipartFile> reviewImgFileList) throws Exception{
        // 기존 질문의 내용을 수정
        reviewBoard.setSubject(subject);
        reviewBoard.setContent(content);
        reviewBoard.setModifiedDate(LocalDateTime.now());
        // 기존 이미지를 삭제하고 새로운 이미지를 추가

        if (reviewImgFileList != null && !reviewImgFileList.isEmpty()) {
            // 기존 이미지를 삭제
            imageService.deleteImagesByReviewBoardId(reviewBoard.getId());

            // 새로운 이미지를 저장
            for (MultipartFile reviewImg : reviewImgFileList) {
                if (reviewImg != null && !reviewImg.isEmpty()) {
                    Image image = new Image();
                    imageService.saveReviewImage(image, reviewImg);
                    image.setReviewBoard(reviewBoard);
                    imageRepository.save(image);
                }
            }
        }

        // 수정된 내용을 저장
        reviewBoardRepository.save(reviewBoard);
    }
    @Transactional(readOnly = true)
    public ReviewBoardForm getReviewBoardDtl(Long reviewBoardId){
        List<Image> reviewImgList = imageRepository.findByReviewBoardIdOrderByIdAsc(reviewBoardId);
        List<ImageDto> reviewImgDtoList = new ArrayList<>();
        for(Image reviewImage : reviewImgList){
            ImageDto reviewImgDto = ImageDto.of(reviewImage);
            reviewImgDtoList.add(reviewImgDto);
        }
        ReviewBoard reviewBoard = reviewBoardRepository.findById(reviewBoardId).
                orElseThrow(EntityNotFoundException::new);

        ReviewBoardForm reviewBoardForm = ReviewBoardForm.of(reviewBoard);
        reviewBoardForm.setReviewImgDtoList(reviewImgDtoList);
        return reviewBoardForm;
    }

    public void vote(ReviewBoard reviewBoard, Member siteUser) {
        reviewBoard.getVoter().add(siteUser);
        notificationService.reviewVoter(reviewBoard);
        this.reviewBoardRepository.save(reviewBoard);
    }

    public void saveReviewBoard(ReviewBoard reviewBoard) {
        reviewBoardRepository.save(reviewBoard);
    }
    public void cancelVote(ReviewBoard reviewBoard, Member siteUser) {
        reviewBoard.getVoter().remove(siteUser);
        this.reviewBoardRepository.save(reviewBoard);
    }
//    public Page<ReviewBoard> getList(int page, String keyword) {
//        Pageable pageable = PageRequest.of(page, 10);
//        return reviewBoardRepository.findAllByKeyword(keyword, pageable);
//    }
public Page<ReviewBoard> getList(int page, String keyword, String sort) {
    Pageable pageable = PageRequest.of(page, 6);

    if (keyword != null && !keyword.isEmpty()) {
        switch (sort) {
            case "views":
                return reviewBoardRepository.findByKeywordOrderByCountDesc(keyword, pageable);
            case "votes":
                return reviewBoardRepository.findByKeywordOrderByVoterCountDesc(keyword, pageable);
            case "date":
            default:
                return reviewBoardRepository.findByKeywordOrderByCreateDateDesc(keyword, pageable);
        }
    } else {
        switch (sort) {
            case "views":
                return reviewBoardRepository.findAllOrderByCountDesc(pageable);
            case "votes":
                return reviewBoardRepository.findAllOrderByVoterCountDesc(pageable);
            case "date":
            default:
                return reviewBoardRepository.findAllOrderByCreateDateDesc(pageable);
        }
    }
}
    private ReviewBoardForm convertToForm(ReviewBoard reviewBoard) {
        List<Image> reviewImgList = imageRepository.findByReviewBoardIdOrderByIdAsc(reviewBoard.getId());
        List<ImageDto> reviewImgDtoList = reviewImgList.stream().map(ImageDto::of).collect(Collectors.toList());

        ReviewBoardForm form = ReviewBoardForm.of(reviewBoard);
        form.setReviewImgDtoList(reviewImgDtoList);
        return form;
    }

    public List<ReviewBoardForm> getTopRatedReviews() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by("voterCount").descending());
        Page<ReviewBoard> topRatedReviewsPage = reviewBoardRepository.findAll(pageable);
        List<ReviewBoard> topRatedReviews = topRatedReviewsPage.getContent();
        return topRatedReviews.stream()
                .map(this::convertToForm)
                .collect(Collectors.toList());
    }


    @Bean
    public CommandLineRunner initReview(ReviewBoardRepository reviewBoardRepository, ImageRepository imageRepository) {
        return args -> {
            boolean isSummerTicketPresent = reviewBoardRepository.findBySubject("MGR 후 석촌호수는 최고의 선택!").isPresent();

            if (!isSummerTicketPresent) {
                Member author = new Member();
                ReviewBoard reviewBoard = new ReviewBoard();
                reviewBoard.setSubject("MGR 후 석촌호수는 최고의 선택!");
                reviewBoard.setContent("오랜만에 MGR 방문했습니다!! 벚꽃도 너무 예쁘고 날씨도 좋아서 굉장히 즐거운 하루였습니다!! MGR에서 신나게 놀다 저녁에 석촌호수를 가니 정말 최고의 코스더라구요~ MGR 방문 예정이신 분들 석촌호수도 꼭 같이 가보시길 바랄게요 강추입니다~~");
                author.setNickname("웃음의 주인공");
                reviewBoard.setAuthor(author);
                reviewBoard.setCreateDate(LocalDateTime.parse("2024-06-02T00:00:00"));

                reviewBoardRepository.save(reviewBoard);

                String reviewImgUrl = "/img/review/lake.jpg";

                Image reviewImage = new Image();
                reviewImage.setReviewBoard(reviewBoard);
                reviewImage.setImgUrl(reviewImgUrl);
                reviewImage.setRepImgYn(true);
                imageRepository.save(reviewImage);
            }
        };
    }
    @Bean
    public CommandLineRunner initReview1(ReviewBoardRepository reviewBoardRepository, ImageRepository imageRepository) {
        return args -> {
            boolean isSummerTicketPresent = reviewBoardRepository.findBySubject("최고의 데이트 장소 MGR!").isPresent();

            if (!isSummerTicketPresent) {
                Member author = new Member();
                ReviewBoard reviewBoard = new ReviewBoard();
                reviewBoard.setSubject("최고의 데이트 장소 MGR");
                reviewBoard.setContent("MGR 놀이공원은 정말 멋진 곳이었어요! 남자친구와 함께 방문해서 너무 즐거운 시간을 보냈습니다. 먼저, 놀이기구는 다양하고 스릴 넘치는 경험을 제공했어요. 특히 '스카이로켓'과 '마운틴블라스트'는 속도감과 함께 높이에서의 전망이 너무 멋졌습니다. 남자친구도 정말 좋아했어요! 그리고 직원들이 너무 친절했어요. 입구에서부터 놀이기구 안내까지 정말 상냥하게 대해주셔서 기분이 좋았습니다. 놀이공원 내부도 깨끗하고 청결하게 관리되어 있어서 좋았어요. 종합적으로: MGR 놀이공원은 재미와 휴식을 동시에 제공하는 완벽한 장소였습니다. 다음에도 또 방문하고 싶어요!");
                author.setNickname("나무늘보");
                reviewBoard.setCreateDate(LocalDateTime.parse("2024-06-09T00:00:00"));
                reviewBoard.setAuthor(author);
                reviewBoardRepository.save(reviewBoard);

                String reviewImgUrl = "/img/review/christmas.jfif";

                Image reviewImage = new Image();
                reviewImage.setReviewBoard(reviewBoard);
                reviewImage.setImgUrl(reviewImgUrl);
                reviewImage.setRepImgYn(true);
                imageRepository.save(reviewImage);
            }
        };
    }

    @Bean
    public CommandLineRunner initReview2(ReviewBoardRepository reviewBoardRepository, ImageRepository imageRepository) {
        return args -> {
            boolean isSummerTicketPresent = reviewBoardRepository.findBySubject("MGR에서 추억쌓기").isPresent();

            if (!isSummerTicketPresent) {
                Member author = new Member();
                ReviewBoard reviewBoard = new ReviewBoard();
                reviewBoard.setSubject("MGR에서 추억쌓기");
                reviewBoard.setContent("MGR에서 보트도 운행한다고 해서 방문했습니다~~ 굉장히 재미있고 시간도 길어서 좋았어요!! 나중에 또오고싶네요~~");
                author.setNickname("영인");
                reviewBoard.setAuthor(author);
                reviewBoard.setCreateDate(LocalDateTime.parse("2024-06-11T00:00:00"));
                reviewBoardRepository.save(reviewBoard);

                String reviewImgUrl = "/img/review/boat.jpg";

                Image reviewImage = new Image();
                reviewImage.setReviewBoard(reviewBoard);
                reviewImage.setImgUrl(reviewImgUrl);
                reviewImage.setRepImgYn(true);
                imageRepository.save(reviewImage);
            }
        };
    }
    @Bean
    public CommandLineRunner initReview3(ReviewBoardRepository reviewBoardRepository, ImageRepository imageRepository) {
        return args -> {
            boolean isSummerTicketPresent = reviewBoardRepository.findBySubject("음식도 맛있는 MGR 자꾸 생각나요~").isPresent();

            if (!isSummerTicketPresent) {
                Member author = new Member();
                ReviewBoard reviewBoard = new ReviewBoard();
                reviewBoard.setSubject("음식도 맛있는 MGR 자꾸 생각나요~");
                reviewBoard.setContent("평소 놀이공원 식당이 비싸다고만 생각했는데 MGR은 다른 놀이공원과 달라서 좋았습니다! 저렴한 가격에 맛있는 음식을 먹을 수 있어서 너무 좋았어요~~ 특히 떡볶이가 너무 맛있어서 나중에 또 오고싶네요!!");
                author.setNickname("신난 강아지");
                reviewBoard.setAuthor(author);
                reviewBoard.setCreateDate(LocalDateTime.parse("2024-06-22T00:00:00"));
                reviewBoardRepository.save(reviewBoard);

                String reviewImgUrl = "/img/review/store.jpg";

                Image reviewImage = new Image();
                reviewImage.setReviewBoard(reviewBoard);
                reviewImage.setImgUrl(reviewImgUrl);
                reviewImage.setRepImgYn(true);
                imageRepository.save(reviewImage);
            }
        };
    }
    @Bean
    public CommandLineRunner initReview4(ReviewBoardRepository reviewBoardRepository, ImageRepository imageRepository) {
        return args -> {
            boolean isSummerTicketPresent = reviewBoardRepository.findBySubject("퍼레이드는 MGR!").isPresent();

            if (!isSummerTicketPresent) {
                Member author = new Member();
                ReviewBoard reviewBoard = new ReviewBoard();
                reviewBoard.setSubject("퍼레이드는 MGR!");
                reviewBoard.setContent("MGR퍼레이드가 굉장히 화려하다고 해서 방문해봤어요~~ 애들도 굉장히 좋아해서 즐겁게 놀았네요^^ 다음에도 가족들과 같이 방문하고싶네요~~");
                author.setNickname("안경 쓴 고양이");
                reviewBoard.setCreateDate(LocalDateTime.parse("2024-06-16T00:00:00"));
                reviewBoard.setAuthor(author);
                reviewBoardRepository.save(reviewBoard);

                String reviewImgUrl = "/img/review/parade.jpg";

                Image reviewImage = new Image();
                reviewImage.setReviewBoard(reviewBoard);
                reviewImage.setImgUrl(reviewImgUrl);
                reviewImage.setRepImgYn(true);
                imageRepository.save(reviewImage);
            }
        };
    }
    @Bean
    public CommandLineRunner initReview5(ReviewBoardRepository reviewBoardRepository, ImageRepository imageRepository) {
        return args -> {
            boolean isSummerTicketPresent = reviewBoardRepository.findBySubject("MGR방문 기록").isPresent();

            if (!isSummerTicketPresent) {
                Member author = new Member();
                ReviewBoard reviewBoard = new ReviewBoard();
                reviewBoard.setSubject("MGR방문 기록");
                reviewBoard.setContent("재밌게 놀고가요~~~~ 밥도 맛있네요^^");
                author.setNickname("잠자는 햄스터");
                reviewBoard.setAuthor(author);
                reviewBoard.setCreateDate(LocalDateTime.parse("2024-06-12T00:00:00"));
                reviewBoardRepository.save(reviewBoard);

                String reviewImgUrl = "/img/review/store2.jfif";

                Image reviewImage = new Image();
                reviewImage.setReviewBoard(reviewBoard);
                reviewImage.setImgUrl(reviewImgUrl);
                reviewImage.setRepImgYn(true);
                imageRepository.save(reviewImage);
            }
        };
    }
    @Bean
    public CommandLineRunner initReview6(ReviewBoardRepository reviewBoardRepository, ImageRepository imageRepository) {
        return args -> {
            boolean isSummerTicketPresent = reviewBoardRepository.findBySubject("믿고가는 MGR!").isPresent();

            if (!isSummerTicketPresent) {
                Member author = new Member();
                ReviewBoard reviewBoard = new ReviewBoard();
                reviewBoard.setSubject("믿고가는 MGR!");
                reviewBoard.setContent("재밌는 놀이기구가 많아서 너무좋았어요~~ 다음에 친구들이랑 또 오기로 했습니다!!!");
                author.setNickname("배고픈 사슴");
                reviewBoard.setCreateDate(LocalDateTime.parse("2024-06-09T00:00:00"));
                reviewBoard.setAuthor(author);
                reviewBoardRepository.save(reviewBoard);

                String reviewImgUrl = "/img/review/attraction1.jpeg";

                Image reviewImage = new Image();
                reviewImage.setReviewBoard(reviewBoard);
                reviewImage.setImgUrl(reviewImgUrl);
                reviewImage.setRepImgYn(true);
                imageRepository.save(reviewImage);
            }
        };
    }
}

