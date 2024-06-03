package com.MGR.dto;

import com.MGR.constant.LocationCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TicketSearchDto {
    private String searchDateType; // 현재시간과 상품등록일을 비교하여 조회

    private String searchPeriodType;

    private String searchBy; // 티켓 조회

    private String searchQuery = ""; // 조회할 검색어를 저장할 변수

    private String searchPrice;

    private LocationCategory locationCategory;
}
