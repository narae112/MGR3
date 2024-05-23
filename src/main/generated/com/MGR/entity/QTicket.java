package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTicket is a Querydsl query type for Ticket
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTicket extends EntityPathBase<Ticket> {

    private static final long serialVersionUID = -744015116L;

    public static final QTicket ticket = new QTicket("ticket");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath endDate = createString("endDate");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath location = createString("location");

    public final StringPath memo = createString("memo");

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final StringPath name = createString("name");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regTime = _super.regTime;

    public final StringPath startDate = createString("startDate");

    public final EnumPath<com.MGR.constant.TicketCategory> ticketCategory = createEnum("ticketCategory", com.MGR.constant.TicketCategory.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateTime = _super.updateTime;

    public QTicket(String variable) {
        super(Ticket.class, forVariable(variable));
    }

    public QTicket(Path<? extends Ticket> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTicket(PathMetadata metadata) {
        super(Ticket.class, metadata);
    }

}

