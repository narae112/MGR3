package com.MGR.entity;

import com.MGR.constant.TicketCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter @Getter
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime startDate;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime endDate;

    @Column
    private String location;

    @Enumerated(EnumType.STRING)
    private TicketCategory ticketCategory;

    @Column
    private String name;

    @Column
    private int price;

    @Column(columnDefinition = "TEXT")
    private String memo;

}
