package cart;

import common.BaseEntity;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CartItem extends BaseEntity {
    private final UUID cartId;
    private final UUID variantId;
    private int quantity;

    public CartItem(UUID cartId, UUID variantId, int quantity) {
        this.cartId = cartId;
        this.variantId = variantId;
        this.quantity = requirePositiveQuantity(quantity);
    }

    public void setQuantity(int quantity) {
        this.quantity = requirePositiveQuantity(quantity);
        markUpdated();
    }

    private static int requirePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Cart item quantity must be greater than 0");
        }
        return quantity;
    }
}

