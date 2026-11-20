package discovery;

import accounts.User;
import catalog.Product;
import common.BaseEntity;
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
import lombok.Setter;
import orders.OrderItem;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "reviews", uniqueConstraints = {
    @UniqueConstraint(name = "uq_reviews_order_item_id", columnNames = {"order_item_id"})
})
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_reviews_user_id"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_reviews_product_id"))
    private Product product;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_reviews_order_item_id"))
    private OrderItem orderItem;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "text", nullable = false, columnDefinition = "text")
    @Setter
    private String text;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "fit_rating", length = 32)
    @Setter
    private FitRating fitRating;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", nullable = false, length = 32)
    @Setter
    private ReviewStatus status = ReviewStatus.PENDING;

    protected Review() {
    }

    public Review(User user, Product product, OrderItem orderItem, int rating, String text) {
        this.user = user;
        this.product = product;
        this.orderItem = orderItem;
        this.rating = requireRating(rating);
        this.text = text;
    }

    public void setRating(int rating) {
        this.rating = requireRating(rating);
        markUpdated();
    }

    private static int requireRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Review rating must be between 1 and 5");
        }
        return rating;
    }
}
