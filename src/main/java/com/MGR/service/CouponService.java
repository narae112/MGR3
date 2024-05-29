package com.MGR.service;

import com.MGR.dto.CouponFormDto;
import com.MGR.dto.CouponMainDto;
import com.MGR.dto.CouponSearchDto;
import com.MGR.entity.Coupon;
import com.MGR.entity.Image;
import com.MGR.exception.DuplicateTicketNameException;
import com.MGR.repository.CouponRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CouponService {

    final CouponRepository couponRepository;
    final ImageService imageService;

    // 쿠폰 생성
    public Long createCoupon(CouponFormDto couponFormDto, List<MultipartFile> couponImgFileList) throws Exception {
        // 중복 데이터 검사
        boolean isDuplicate = isDuplicateCoupon(couponFormDto);
        if (isDuplicate) {
            throw new DuplicateTicketNameException("중복된 쿠폰 정보가 존재합니다");
        }

        // 쿠폰 저장
        Coupon coupon = Coupon.createCoupon(
                couponFormDto.getName(), couponFormDto.getDiscountRate(),
                couponFormDto.getStartDate(), couponFormDto.getEndDate(), couponFormDto.getCouponContent());
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
        Optional<Coupon> couponOptional = couponRepository.findByNameAndDiscountRateAndStartDateAndEndDate(
                couponFormDto.getName(), couponFormDto.getDiscountRate(), couponFormDto.getStartDate(), couponFormDto.getEndDate()
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

    @Transactional(readOnly = true)
    public Page<CouponMainDto> getCouponMainPage(CouponSearchDto couponSearchDto, Pageable pageable){
        return couponRepository.getCouponMainPage(couponSearchDto, pageable);
    }

}
