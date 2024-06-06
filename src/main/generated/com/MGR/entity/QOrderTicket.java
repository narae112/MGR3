package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrderTicket is a Querydsl query type for OrderTicket
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderTicket extends EntityPathBase<OrderTicket> {

    private static final long serialVersionUID = 1213369618L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrderTicket orderTicket = new QOrderTicket("orderTicket");

    public final NumberPath<Integer> adultCount = createNumber("adultCount", Integer.class);

    public final NumberPath<Integer> adultPrice = createNumber("adultPrice", Integer.class);

    public final NumberPath<Integer> childCount = createNumber("childCount", Integer.class);

    public final NumberPath<Integer> childPrice = createNumber("childPrice", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QOrder order;

    public final NumberPath<Long> reservationTicketId = createNumber("reservationTicketId", Long.class);

    public final QTicket ticket;

    public final DatePath<java.time.LocalDate> visitDate = createDate("visitDate", java.time.LocalDate.class);

    public QOrderTicket(String variable) {
        this(OrderTicket.class, forVariable(variable), INITS);
    }

    public QOrderTicket(Path<? extends OrderTicket> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrderTicket(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrderTicket(PathMetadata metadata, PathInits inits) {
        this(OrderTicket.class, metadata, inits);
    }

    public QOrderTicket(Class<? extends OrderTicket> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.order = inits.isInitialized("order") ? new QOrder(forProperty("order"), inits.get("order")) : null;
        this.ticket = inits.isInitialized("ticket") ? new QTicket(forProperty("ticket")) : null;
    }

}

