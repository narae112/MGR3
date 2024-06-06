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

    public final QMember author;

    public final ListPath<ReviewComment, QReviewComment> commentList = this.<ReviewComment, QReviewComment>createList("commentList", ReviewComment.class, QReviewComment.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<Image, QImage> images = this.<Image, QImage>createList("images", Image.class, QImage.class, PathInits.DIRECT2);

    public final ListPath<String, StringPath> imageUrls = this.<String, StringPath>createList("imageUrls", String.class, StringPath.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final StringPath subject = createString("subject");

    public final SetPath<Member, QMember> voter = this.<Member, QMember>createSet("voter", Member.class, QMember.class, PathInits.DIRECT2);

    public final NumberPath<Integer> voterCount = createNumber("voterCount", Integer.class);

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
        this.author = inits.isInitialized("author") ? new QMember(forProperty("author")) : null;
    }

}

