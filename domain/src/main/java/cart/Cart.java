package cart;

import cart.enums.CartStatus;
import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class Cart extends BaseEntity {
    private UUID userId; // Optional; exactly one owner is required.
    private UUID guestId; // Optional; anonymous identifier, not a User reference.

    @Setter
    private UUID promoCodeId; // Optional.

    @Setter
    private CartStatus status = CartStatus.ACTIVE;

    public Cart(UUID userId, UUID guestId) {
        if ((userId == null) == (guestId == null)) {
            throw new IllegalArgumentException("Cart must have either userId or guestId, but not both");
        }
        this.userId = userId;
        this.guestId = guestId;
    }

    public static Cart forUser(UUID userId) {
        return new Cart(Objects.requireNonNull(userId, "userId"), null);
    }

    public static Cart forGuest(UUID guestId) {
        return new Cart(null, Objects.requireNonNull(guestId, "guestId"));
    }

    public void assignToUser(UUID userId) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.guestId = null;
        markUpdated();
    }
}

