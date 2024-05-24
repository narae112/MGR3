package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQnaQuestion is a Querydsl query type for QnaQuestion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQnaQuestion extends EntityPathBase<QnaQuestion> {

    private static final long serialVersionUID = -2099571966L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQnaQuestion qnaQuestion = new QQnaQuestion("qnaQuestion");

    public final ListPath<QnaAnswer, QQnaAnswer> answerList = this.<QnaAnswer, QQnaAnswer>createList("answerList", QnaAnswer.class, QQnaAnswer.class, PathInits.DIRECT2);

    public final QMember author;

    public final StringPath content = createString("content");

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final StringPath subject = createString("subject");

    public final StringPath title = createString("title");

    public final SetPath<Member, QMember> voter = this.<Member, QMember>createSet("voter", Member.class, QMember.class, PathInits.DIRECT2);

    public QQnaQuestion(String variable) {
        this(QnaQuestion.class, forVariable(variable), INITS);
    }

    public QQnaQuestion(Path<? extends QnaQuestion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQnaQuestion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQnaQuestion(PathMetadata metadata, PathInits inits) {
        this(QnaQuestion.class, metadata, inits);
    }

    public QQnaQuestion(Class<? extends QnaQuestion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new QMember(forProperty("author")) : null;
    }

}

