package com.MGR.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
public class ReservationTicketDto {
    @NotNull(message = "티켓 아이디는 필수 입력값입니다")
    private Long ticketId;

    @NotNull(message = "방문 예정일을 선택해주세요")
    private String visitDate;

    @Min(value = 1, message = "성인 인원 수를 입력해주세요")
    @Max(value = 99, message = "최대 주문 수량은 99개입니다")
    private int adultCount;

    @Min(value = 0)
    @Max(value = 99, message = "최대 주문 수량은 99개입니다")
    private int childCount;

    public int getTicketCount() {
        return adultCount + childCount;
    }
}