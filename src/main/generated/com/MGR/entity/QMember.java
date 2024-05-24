package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -947823998L;

    public static final QMember member = new QMember("member1");

    public final ListPath<QnaAnswer, QQnaAnswer> answers = this.<QnaAnswer, QQnaAnswer>createList("answers", QnaAnswer.class, QQnaAnswer.class, PathInits.DIRECT2);

    public final StringPath birth = createString("birth");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isSuspended = createBoolean("isSuspended");

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final ListPath<QnaQuestion, QQnaQuestion> questions = this.<QnaQuestion, QQnaQuestion>createList("questions", QnaQuestion.class, QQnaQuestion.class, PathInits.DIRECT2);

    public final EnumPath<com.MGR.constant.Role> role = createEnum("role", com.MGR.constant.Role.class);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

