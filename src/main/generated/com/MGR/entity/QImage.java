package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QImage is a Querydsl query type for Image
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QImage extends EntityPathBase<Image> {

    private static final long serialVersionUID = -726778765L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QImage image = new QImage("image");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QCoupon coupon;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final QEventBoard eventBoard;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imgName = createString("imgName");

    public final StringPath imgOriName = createString("imgOriName");

    public final StringPath imgUrl = createString("imgUrl");

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    public final QQnaQuestion qnaQuestion;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regTime = _super.regTime;

    public final BooleanPath repImgYn = createBoolean("repImgYn");

    public final QTicket ticket;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateTime = _super.updateTime;

    public QImage(String variable) {
        this(Image.class, forVariable(variable), INITS);
    }

    public QImage(Path<? extends Image> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QImage(PathMetadata metadata, PathInits inits) {
        this(Image.class, metadata, inits);
    }

    public QImage(Class<? extends Image> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupon = inits.isInitialized("coupon") ? new QCoupon(forProperty("coupon")) : null;
        this.eventBoard = inits.isInitialized("eventBoard") ? new QEventBoard(forProperty("eventBoard"), inits.get("eventBoard")) : null;
        this.qnaQuestion = inits.isInitialized("qnaQuestion") ? new QQnaQuestion(forProperty("qnaQuestion"), inits.get("qnaQuestion")) : null;
        this.ticket = inits.isInitialized("ticket") ? new QTicket(forProperty("ticket")) : null;
    }

}

