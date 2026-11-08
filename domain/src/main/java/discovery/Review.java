package discovery;

import common.BaseEntity;
import discovery.enums.FitRating;
import discovery.enums.ReviewStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class Review extends BaseEntity {
    private final UUID userId;
    private final UUID productId;
    private final UUID orderItemId;
    private int rating;

    @Setter
    private String text;

    @Setter
    private FitRating fitRating; // Optional.

    @Setter
    private ReviewStatus status = ReviewStatus.PENDING;

    public Review(UUID userId, UUID productId, UUID orderItemId, int rating, String text) {
        this.userId = userId;
        this.productId = productId;
        this.orderItemId = orderItemId;
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

