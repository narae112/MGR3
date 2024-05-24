package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReservationTicket is a Querydsl query type for ReservationTicket
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReservationTicket extends EntityPathBase<ReservationTicket> {

    private static final long serialVersionUID = 1620263184L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReservationTicket reservationTicket = new QReservationTicket("reservationTicket");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QReservation reservation;

    public final DateTimePath<java.time.LocalDateTime> reservationDate = createDateTime("reservationDate", java.time.LocalDateTime.class);

    public final QTicket ticket;

    public final NumberPath<Integer> ticketCount = createNumber("ticketCount", Integer.class);

    public final DatePath<java.time.LocalDate> visitDate = createDate("visitDate", java.time.LocalDate.class);

    public QReservationTicket(String variable) {
        this(ReservationTicket.class, forVariable(variable), INITS);
    }

    public QReservationTicket(Path<? extends ReservationTicket> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReservationTicket(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReservationTicket(PathMetadata metadata, PathInits inits) {
        this(ReservationTicket.class, metadata, inits);
    }

    public QReservationTicket(Class<? extends ReservationTicket> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reservation = inits.isInitialized("reservation") ? new QReservation(forProperty("reservation"), inits.get("reservation")) : null;
        this.ticket = inits.isInitialized("ticket") ? new QTicket(forProperty("ticket")) : null;
    }

}

