package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotification is a Querydsl query type for Notification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = -12072237L;

    public static final QNotification notification = new QNotification("notification");

    public final NumberPath<Long> boardId = createNumber("boardId", Long.class);

    public final NumberPath<Long> couponId = createNumber("couponId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final StringPath message = createString("message");

    public final NumberPath<Long> paymentId = createNumber("paymentId", Long.class);

    public final NumberPath<Long> reviewCommentId = createNumber("reviewCommentId", Long.class);

    public final NumberPath<Long> reviewId = createNumber("reviewId", Long.class);

    public final StringPath type = createString("type");

    public QNotification(String variable) {
        super(Notification.class, forVariable(variable));
    }

    public QNotification(Path<? extends Notification> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotification(PathMetadata metadata) {
        super(Notification.class, metadata);
    }

}

