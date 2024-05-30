package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEventBoard is a Querydsl query type for EventBoard
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEventBoard extends EntityPathBase<EventBoard> {

    private static final long serialVersionUID = -1550469036L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEventBoard eventBoard = new QEventBoard("eventBoard");

    public final StringPath content = createString("content");

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final StringPath endDate = createString("endDate");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isEventCurrent = createBoolean("isEventCurrent");

    public final QMember member;

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final StringPath startDate = createString("startDate");

    public final StringPath title = createString("title");

    public final EnumPath<com.MGR.constant.EventType> type = createEnum("type", com.MGR.constant.EventType.class);

    public QEventBoard(String variable) {
        this(EventBoard.class, forVariable(variable), INITS);
    }

    public QEventBoard(Path<? extends EventBoard> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEventBoard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEventBoard(PathMetadata metadata, PathInits inits) {
        this(EventBoard.class, metadata, inits);
    }

    public QEventBoard(Class<? extends EventBoard> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

