package com.MGR.dto;

import com.MGR.constant.TicketCategory;
import com.MGR.entity.Ticket;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TicketFormDto {
    private Long id;

    @NotBlank(message = "티켓명은 필수 입력 값입니다.")
    private String name;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotBlank(message = "티켓 세부사항은 필수 입력 값입니다.")
    private String memo;

    @NotBlank(message = "지점은 필수 입력 값입니다.")
    private String location;

    @NotNull(message = "시작 날짜는 필수 입력 값입니다.")
    private LocalDateTime startDate;

    @NotNull(message = "종료 날짜는 필수 입력 값입니다.")
    private LocalDateTime endDate;

    private TicketCategory ticketCategory;

    private List<ImageDto> ticketImgDtoList = new ArrayList<>();
    private List<Long> ticketImgIds = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    public Ticket createTicket() {
        return modelMapper.map(this, Ticket.class);
    }

    public static TicketFormDto of(Ticket ticket) {
        return modelMapper.map(ticket, TicketFormDto.class);
    }
}