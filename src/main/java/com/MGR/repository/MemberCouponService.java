package com.MGR.repository;

import com.MGR.entity.Attraction;
import com.MGR.entity.MemberCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberCouponService {

    private final MemberCouponRepository memberCouponRepository;


    public Page<MemberCoupon> getCouponList(Integer page, Long id) {

        List<MemberCoupon> memberCouponList = memberCouponRepository.findAllByMemberId(id);

        // 사용 여부와 만료일 기준으로 정렬
        List<MemberCoupon> sortedList = memberCouponList.stream()
                .sorted(Comparator.comparing(MemberCoupon::isUsed)
                        .thenComparing(memberCoupon -> memberCoupon.getCoupon().getEndDate()))
                .toList();

        //페이징 처리
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));

        // 페이지 처리를 위한 start 와 end 계산
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedList.size());

        // 서브리스트를 Page 객체로 변환
        List<MemberCoupon> pagedList = new ArrayList<>();
        if (start <= end) {
            pagedList = sortedList.subList(start, end);
        }

        return new PageImpl<>(pagedList, pageable, sortedList.size());
    }
}

