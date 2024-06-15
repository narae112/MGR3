package com.MGR.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberCoupon is a Querydsl query type for MemberCoupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberCoupon extends EntityPathBase<MemberCoupon> {

    private static final long serialVersionUID = -1457015480L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberCoupon memberCoupon = new QMemberCoupon("memberCoupon");

    public final QCoupon coupon;

    public final StringPath couponCode = createString("couponCode");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final QOrder order;

    public final BooleanPath used = createBoolean("used");

    public QMemberCoupon(String variable) {
        this(MemberCoupon.class, forVariable(variable), INITS);
    }

    public QMemberCoupon(Path<? extends MemberCoupon> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberCoupon(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberCoupon(PathMetadata metadata, PathInits inits) {
        this(MemberCoupon.class, metadata, inits);
    }

    public QMemberCoupon(Class<? extends MemberCoupon> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupon = inits.isInitialized("coupon") ? new QCoupon(forProperty("coupon"), inits.get("coupon")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.order = inits.isInitialized("order") ? new QOrder(forProperty("order"), inits.get("order")) : null;
    }

}

