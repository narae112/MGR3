package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAttraction is a Querydsl query type for Attraction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAttraction extends EntityPathBase<Attraction> {

    private static final long serialVersionUID = 1964571471L;

    public static final QAttraction attraction = new QAttraction("attraction");

    public final NumberPath<Integer> closureDay = createNumber("closureDay", Integer.class);

    public final BooleanPath condition = createBoolean("condition");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath information = createString("information");

    public final StringPath name = createString("name");

    public QAttraction(String variable) {
        super(Attraction.class, forVariable(variable));
    }

    public QAttraction(Path<? extends Attraction> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAttraction(PathMetadata metadata) {
        super(Attraction.class, metadata);
    }

}

