package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewBoard is a Querydsl query type for ReviewBoard
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewBoard extends EntityPathBase<ReviewBoard> {

    private static final long serialVersionUID = 897761158L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewBoard reviewBoard = new QReviewBoard("reviewBoard");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final QMember member;

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final ListPath<ReviewComment, QReviewComment> reviewCommentList = this.<ReviewComment, QReviewComment>createList("reviewCommentList", ReviewComment.class, QReviewComment.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QReviewBoard(String variable) {
        this(ReviewBoard.class, forVariable(variable), INITS);
    }

    public QReviewBoard(Path<? extends ReviewBoard> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewBoard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewBoard(PathMetadata metadata, PathInits inits) {
        this(ReviewBoard.class, metadata, inits);
    }

    public QReviewBoard(Class<? extends ReviewBoard> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

