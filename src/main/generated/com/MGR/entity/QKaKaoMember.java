package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QKaKaoMember is a Querydsl query type for KaKaoMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QKaKaoMember extends EntityPathBase<KaKaoMember> {

    private static final long serialVersionUID = -153935307L;

    public static final QKaKaoMember kaKaoMember = new QKaKaoMember("kaKaoMember");

    public final StringPath birth = createString("birth");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isSuspended = createBoolean("isSuspended");

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final EnumPath<com.MGR.constant.Role> role = createEnum("role", com.MGR.constant.Role.class);

    public QKaKaoMember(String variable) {
        super(KaKaoMember.class, forVariable(variable));
    }

    public QKaKaoMember(Path<? extends KaKaoMember> path) {
        super(path.getType(), path.getMetadata());
    }

    public QKaKaoMember(PathMetadata metadata) {
        super(KaKaoMember.class, metadata);
    }

}

