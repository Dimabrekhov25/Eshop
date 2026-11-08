package discovery;

import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class StockAlert extends BaseEntity {
    private final UUID variantId;
    private final String email;
    private Instant notifiedAt; // Optional.

    public StockAlert(UUID variantId, String email) {
        this.variantId = variantId;
        this.email = email;
    }
}

