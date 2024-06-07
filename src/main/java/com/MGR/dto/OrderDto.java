package com.MGR.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class OrderDto {
    @NotNull(message = "티켓 아이디는 필수 입력값입니다")
    private Long ticketId;

    @NotNull(message = "성인 수량은 필수 입력값입니다")
    @Min(value = 1, message = "최소 주문 수량은 1개입니다")
    @Max(value = 99, message = "최대 주문 수량은 99개입니다")
    private Integer adultCount;

    @NotNull(message = "아동 수량은 필수 입력값입니다")
    @Min(value = 0, message = "최소 주문 수량은 0개입니다")
    @Max(value = 99, message = "최대 주문 수량은 99개입니다")
    private Integer childCount;

    private LocalDate visitDate;

    private Long reservationTicketId;
}