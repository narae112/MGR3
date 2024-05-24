package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQnAComment is a Querydsl query type for QnAComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQnAComment extends EntityPathBase<QnAComment> {

    private static final long serialVersionUID = -262002717L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQnAComment qnAComment = new QQnAComment("qnAComment");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final QQnABoard qnABoard;

    public QQnAComment(String variable) {
        this(QnAComment.class, forVariable(variable), INITS);
    }

    public QQnAComment(Path<? extends QnAComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQnAComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQnAComment(PathMetadata metadata, PathInits inits) {
        this(QnAComment.class, metadata, inits);
    }

    public QQnAComment(Class<? extends QnAComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.qnABoard = inits.isInitialized("qnABoard") ? new QQnABoard(forProperty("qnABoard"), inits.get("qnABoard")) : null;
    }

}

