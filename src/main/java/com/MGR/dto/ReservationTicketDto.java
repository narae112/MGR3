package com.MGR.dto;

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

    @Min(value = 1, message = "인원 수를 입력해주세요")
    private int ticketCount;

    @NotNull(message = "방문 예정일을 선택해주세요")
    private String visitDate;
}
