package com.MGR.service;

import com.MGR.constant.CouponType;
import com.MGR.dto.*;
import com.MGR.entity.Coupon;
import com.MGR.entity.Image;
import com.MGR.entity.Member;
import com.MGR.entity.MemberCoupon;
import com.MGR.exception.DuplicateTicketNameException;
import com.MGR.repository.CouponRepository;
import com.MGR.repository.ImageRepository;
import com.MGR.repository.MemberCouponRepository;
import com.MGR.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CouponService {

    final CouponRepository couponRepository;
    final ImageService imageService;
    final ImageRepository imageRepository;
    final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final MemberService memberService;
    private final MemberCouponRepository memberCouponRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void sendBirthdayCoupons() {
        LocalDate currentDate = LocalDate.now();
        List<Member> membersWithBirthdayToday = memberService.findMembersWithBirthdayToday();
        List<Coupon> birthdayCoupons = couponRepository.findByCouponType(CouponType.BIRTH);

        // 쿠폰의 할인율을 설정합니다.
        Integer discountRate = 10; // 10%

        for (Coupon coupon : birthdayCoupons) {
            coupon.setDiscountRate(discountRate);
            couponRepository.save(coupon);
        }

        for (Member member : membersWithBirthdayToday) {
            for (Coupon coupon : birthdayCoupons) {
                MemberCoupon memberCoupon = MemberCoupon.memberGetCoupon(member, coupon);
                memberCouponRepository.save(memberCoupon);
                notificationService.notifyCoupon(coupon, memberCoupon, member);
            }
        }
    }
    
     @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void deleteExpiredCoupons() {
        LocalDate currentDate = LocalDate.now();
        List<Coupon> expiredCoupons = couponRepository.findByEndDateBefore(currentDate);
        // 현재 날짜보다 유효기간이 이전인 쿠폰들을 조회

        for (Coupon coupon : expiredCoupons) {
            couponRepository.delete(coupon);
        }
        // 조회된 쿠폰들을 삭제
    }
    // 쿠폰 생성
    public Long createCoupon(CouponFormDto couponFormDto,
                             List<MultipartFile> couponImgFileList) throws Exception {
        // 중복 데이터 검사
        boolean isDuplicate = isDuplicateCoupon(couponFormDto);
        if (isDuplicate) {
            throw new DuplicateTicketNameException("중복된 쿠폰 정보가 존재합니다");
        }

        // 쿠폰 저장
        Coupon coupon = Coupon.createCoupon(
                couponFormDto.getName(), couponFormDto.getDiscountType(), couponFormDto.getStartDate(), couponFormDto.getEndDate(),
                couponFormDto.getCouponContent(), couponFormDto.getCouponType(), couponFormDto.getDiscountRate(), couponFormDto.getDiscountAmount());// 쿠폰 타입 추가
        couponRepository.save(coupon);

        // 이미지 저장
        for (int i = 0; i < couponImgFileList.size(); i++) {
            Image couponImage = new Image();
            couponImage.setCoupon(coupon);

            if (i == 0) {
                couponImage.setRepImgYn(true);
            } else {
                couponImage.setRepImgYn(false);
            }
            imageService.saveCouponImage(couponImage, couponImgFileList.get(i));
        }

        // 사용자에게 쿠폰 할당 및 알림
        List<Member> memberList;
        memberList = memberService.findByAllUser();
//        if (coupon.getCouponType() == CouponType.BIRTH) {
//            memberList = memberService.findMembersWithBirthdayToday();
//        } else if(coupon.getCouponType() == CouponType.ALL){
//
//        }else{
//            throw new IllegalArgumentException("Coupon type is null or invalid.");
//        }
        if (memberList == null) {
            // 적절한 예외 처리 또는 기본값 설정
            throw new IllegalStateException("Member list is null.");
        }
        for (Member member : memberList) {
            MemberCoupon memberCoupon = MemberCoupon.memberGetCoupon(member, coupon);
            memberCouponRepository.save(memberCoupon);
            notificationService.notifyCoupon(coupon, memberCoupon, member);
        }

        return coupon.getId();

    }

    // 쿠폰 수정
    public Long updateCoupon(CouponFormDto couponFormDto, List<MultipartFile> couponImgFileList) throws Exception {
        boolean isDuplicate = isDuplicateCoupon(couponFormDto);
        if (isDuplicate) {
            throw new DuplicateTicketNameException("동일한 쿠폰 정보가 존재합니다.");
        }

        // 수정할 쿠폰을 데이터베이스에서 가져온다
        Coupon couponToUpdate = couponRepository.findById(couponFormDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("쿠폰을 찾을 수 없습니다. ID: " + couponFormDto.getName()));

        // 쿠폰 정보 업데이트
        couponToUpdate.updateCoupon(couponFormDto);
        couponRepository.save(couponToUpdate);

        // 이미지 업데이트
        List<Long> couponImgIds = couponFormDto.getCouponImgIds();
        for (int i = 0; i < couponImgIds.size(); i++) {
            Long imgId = couponImgIds.get(i);
            MultipartFile imgFile = couponImgFileList.get(i);
            imageService.updateCouponImage(imgId, imgFile);
        }

        return couponToUpdate.getId();
    }

    // 중복 데이터 검사
    private boolean isDuplicateCoupon(CouponFormDto couponFormDto) {
        Optional<Coupon> couponOptional = couponRepository.findByNameAndStartDateAndEndDate(
                couponFormDto.getName(), couponFormDto.getStartDate(), couponFormDto.getEndDate()
        );
        return couponOptional.isPresent();
    }

    // 쿠폰 삭제
    public void deleteCoupon(Long couponId) {
        // 쿠폰에 연결된 이미지를 먼저 삭제
        imageService.deleteImagesByCouponId(couponId);

        // 쿠폰 삭제
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(EntityNotFoundException::new);
        couponRepository.delete(coupon);
    }

    @Transactional(readOnly = true)
    public Page<Coupon> getAdminCouponPage(CouponSearchDto couponSearchDto, Pageable pageable){
        return couponRepository.getAdminCouponPage(couponSearchDto, pageable);
    }

//    @Transactional(readOnly = true)
//    public Page<CouponMainDto> getCouponMainPage(CouponSearchDto couponSearchDto, Pageable pageable){
//        return couponRepository.getCouponMainPage(couponSearchDto, pageable);
//    }
    @Transactional(readOnly = true)
    public CouponFormDto getCouponDtl(Long couponId){
        List<Image> couponImgList = imageRepository.findByCouponIdOrderByIdAsc(couponId);
        List<ImageDto> couponImgDtoList = new ArrayList<>();
        for(Image couponImage : couponImgList){
            ImageDto couponImgDto = ImageDto.of(couponImage);
            couponImgDtoList.add(couponImgDto);
        }
        Coupon coupon = couponRepository.findById(couponId).
                orElseThrow(EntityNotFoundException::new);

        CouponFormDto couponFormDto = CouponFormDto.of(coupon);
        couponFormDto.setCouponImgDtoList(couponImgDtoList);
        return couponFormDto;
    }

    public Coupon findById(Long couponId) {
        return couponRepository.findById(couponId).orElseThrow(() -> new NoSuchElementException("쿠폰서비스 최하단, 쿠폰아이디 못찾음: " + couponId));
    }
}
