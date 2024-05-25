package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQnaAnswer is a Querydsl query type for QnaAnswer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQnaAnswer extends EntityPathBase<QnaAnswer> {

    private static final long serialVersionUID = -345625126L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQnaAnswer qnaAnswer = new QQnaAnswer("qnaAnswer");

    public final QMember author;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final QQnaQuestion qnaQuestion;

    public QQnaAnswer(String variable) {
        this(QnaAnswer.class, forVariable(variable), INITS);
    }

    public QQnaAnswer(Path<? extends QnaAnswer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQnaAnswer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQnaAnswer(PathMetadata metadata, PathInits inits) {
        this(QnaAnswer.class, metadata, inits);
    }

    public QQnaAnswer(Class<? extends QnaAnswer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new QMember(forProperty("author")) : null;
        this.qnaQuestion = inits.isInitialized("qnaQuestion") ? new QQnaQuestion(forProperty("qnaQuestion"), inits.get("qnaQuestion")) : null;
    }

}

