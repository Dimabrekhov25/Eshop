package discovery;

import common.BaseEntity;
import lombok.Getter;
import java.util.UUID;

@Getter
public class WishlistItem extends BaseEntity {
    private final UUID userId;
    private final UUID productId;

    public WishlistItem(UUID userId, UUID productId) {
        this.userId = userId;
        this.productId = productId;
    }
}

