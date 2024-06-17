package com.MGR.service;

import com.MGR.constant.AgeCategory;
import com.MGR.constant.LocationCategory;
import com.MGR.entity.GoWithBoard;
import com.MGR.entity.Image;
import com.MGR.entity.Member;
import com.MGR.entity.ReviewBoard;
import com.MGR.repository.GoWithBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GoWithBoardService {
    private final GoWithBoardRepository goWithBoardRepository;
    private final ImageService imageService;
    private final MemberService memberService;
    private final NotificationService notificationService;

    public Long createGoWithBoard(Member user, String title, String content, String wantDate, LocationCategory locationCategory,
                                  AgeCategory ageCategory, List<String> attractionTypes, List<String> afterTypes,
                                  List<String> personalities, List<MultipartFile> goWithImgFileList) throws Exception {

        GoWithBoard goWithBoard = new GoWithBoard();

        goWithBoard.setMember(user);
        goWithBoard.setCreateDate(LocalDateTime.now());
        goWithBoard.setWantDate(wantDate);
        goWithBoard.setLocationCategory(locationCategory);
        goWithBoard.setAgeCategory(ageCategory);
        goWithBoard.setAttractionTypes(attractionTypes != null ? String.join(",", attractionTypes) : "");
        goWithBoard.setAfterTypes(afterTypes != null ? String.join(",", afterTypes) : "");
        goWithBoard.setPersonalities(personalities != null ? String.join(",", personalities) : "");

        goWithBoard.setTitle(title);
        goWithBoard.setContent(content);

        goWithBoardRepository.save(goWithBoard);

        List<Member> matchingAfterMembers = goWithBoardRepository.findMembersBySimilarAfterTypes(goWithBoard.getAfterTypes());
        List<Member> matchingAttractionMembers = goWithBoardRepository.findMembersBySimilarAttractions(goWithBoard.getAttractionTypes());
        List<Member> matchingPersonalityMembers = goWithBoardRepository.findMembersBySimilarPersonalities(goWithBoard.getPersonalities());

// 알림을 보낸 멤버를 추적하기 위한 집합을 만듭니다
        Set<Long> notifiedMembers = new HashSet<>();

// matchingAfterMembers 중 본인이 아닌 멤버에게 알림을 보냅니다
        for (Member member : matchingAfterMembers) {
            if (!member.getId().equals(goWithBoard.getMember().getId()) && notifiedMembers.add(member.getId())) {
                notificationService.goWithBoard(goWithBoard, member);
            }
        }

// matchingAttractionMembers 중 본인이 아닌 멤버에게 알림을 보냅니다
        for (Member member : matchingAttractionMembers) {
            if (!member.getId().equals(goWithBoard.getMember().getId()) && notifiedMembers.add(member.getId())) {
                notificationService.goWithBoard(goWithBoard, member);
            }
        }

// matchingPersonalityMembers 중 본인이 아닌 멤버에게 알림을 보냅니다
        for (Member member : matchingPersonalityMembers) {
            if (!member.getId().equals(goWithBoard.getMember().getId()) && notifiedMembers.add(member.getId())) {
                notificationService.goWithBoard(goWithBoard, member);
            }
        }


        for (int i = 0; i < goWithImgFileList.size(); i++) {
            Image goWithImage = new Image();
            goWithImage.setGoWithBoard(goWithBoard);
            if (i == 0) {
                goWithImage.setRepImgYn(true);
            } else {
                goWithImage.setRepImgYn(false);
            }

            if(!goWithImgFileList.get(i).isEmpty()) {
                imageService.saveGoWithImage(goWithImage, goWithImgFileList.get(i));
            }
        }
        return goWithBoard.getId();
    }
}
