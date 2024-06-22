package com.MGR.service;


import com.MGR.entity.Image;
import com.MGR.entity.Member;
import com.MGR.entity.Order;
import com.MGR.exception.DataNotFoundException;
import com.MGR.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    //멤버 저장
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    //닉네임 중복체크
    public int nicknameCheck(String nickname){
        Optional<Member> member = memberRepository.findByNickname(nickname);
        int result = member.isPresent()? 1 : 0; //1이면 중복

        return result;
    }

    //이메일 중복 체크
    public int emailCheck(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        int result = member.isPresent()? 1 : 0; //DB에 동일한 이메일이 없으면 1

        return result;
    }

    public Optional<Member> findById(Long id){
        return memberRepository.findById(id);
    }
//qna question
 public Member getUser(String email) {
    Optional<Member> member = memberRepository.findByEmail(email);
    if (member.isPresent()) {
        return member.get();
    } else {
        throw new DataNotFoundException("siteuser not found");
    }
}


    public void updateNickname(Long id, String nickname) {
        Member member = memberRepository.findById(id).get();
        member.setNickname(nickname);
    }

    public void updatePassword(Long id, String password) {
        Member member = memberRepository.findById(id).get();
        member.setPassword(passwordEncoder.encode(password));
    }

    public List<Member> findByAllMembers() {
        return memberRepository.findAll();
    }

//    public Page<Member> getAllMembers(Integer page) {
//        List<Member> memberList = memberRepository.findAll();
//
//        List<Member> sortedList = memberList.stream()
//                .sorted(Comparator.comparing(Member::getId))
//                .toList();
//
//        List<Sort.Order> sorts = new ArrayList<>();
//        sorts.add(Sort.Order.desc("id"));
//        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
//
//        int start = (int) pageable.getOffset();
//        int end = Math.min((start + pageable.getPageSize()), sortedList.size());
//
//        List<Member> pagedList = new ArrayList<>();
//        if (start <= end) {
//            pagedList = sortedList.subList(start, end);
//        }
//
//        return new PageImpl<>(pagedList, pageable, sortedList.size());
//    }

//    public Page<Member> getAllMembers(Integer page) {
//        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("id")));
//        return memberRepository.findAll(pageable);
//    }

    public Page<Member> getAllMembers(Integer page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("id")));
        return memberRepository.findByRole("ROLE_USER", pageable);
    }

    public List<Member> findByAllUser() {
        //member 의 role 이 User 인 것만 찾기
        return memberRepository.findAll()
                .stream()
                .filter(member -> "ROLE_USER".equals(member.getRole()))
                .collect(Collectors.toList());
    }
     public List<Member> findMembersWithBirthdayToday() {
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
         String todayStr = LocalDate.now().format(formatter);
        return memberRepository.findByBirth(todayStr);
    }


    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public String saveProfileImg(Member member, MultipartFile profileImgFile) throws Exception {
        Image image = imageService.findByMember(member);

        if (image == null) {
            image = new Image();
            image.setMember(member);
        }

        imageService.saveProfileImage(image, profileImgFile);
        return image.getImgUrl(); // 이미지 URL 반환
    }

    public Optional<Member> findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname);
    }

}
