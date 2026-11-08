package catalog;

import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Inventory {
    private final UUID variantId;
    private int stockQuantity;
    private Instant updatedAt;

    public Inventory(UUID variantId, int stockQuantity) {
        this.variantId = Objects.requireNonNull(variantId, "variantId");
        this.stockQuantity = requireStockQuantity(stockQuantity);
        this.updatedAt = Instant.now();
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = requireStockQuantity(stockQuantity);
        this.updatedAt = Instant.now();
    }

    private static int requireStockQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity must not be negative");
        }
        return quantity;
    }
}

