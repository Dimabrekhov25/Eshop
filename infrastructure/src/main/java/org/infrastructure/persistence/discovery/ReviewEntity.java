package org.infrastructure.persistence.discovery;

import discovery.enums.FitRating;
import discovery.enums.ReviewStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.infrastructure.persistence.accounts.UserEntity;
import org.infrastructure.persistence.catalog.ProductEntity;
import org.infrastructure.persistence.common.BaseJpaEntity;
import org.infrastructure.persistence.orders.OrderItemEntity;

@Getter
@Entity
@Table(name = "reviews", uniqueConstraints = {
    @UniqueConstraint(name = "uq_reviews_order_item_id", columnNames = {"order_item_id"})
})
public class ReviewEntity extends BaseJpaEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_reviews_user_id"))
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_reviews_product_id"))
    private ProductEntity product;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_reviews_order_item_id"))
    private OrderItemEntity orderItem;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "text", nullable = false, columnDefinition = "text")
    private String text;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "fit_rating", length = 32)
    private FitRating fitRating;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", nullable = false, length = 32)
    private ReviewStatus status;

    protected ReviewEntity() {
    }
}

