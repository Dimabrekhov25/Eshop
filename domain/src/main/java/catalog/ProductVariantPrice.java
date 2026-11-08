package catalog;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
public class ProductVariantPrice {
    private final UUID variantId;
    private final String currency;
    private BigDecimal price;
    private BigDecimal oldPrice; // Optional.

    public ProductVariantPrice(UUID variantId, String currency, BigDecimal price) {
        this.variantId = Objects.requireNonNull(variantId, "variantId");
        this.currency = Objects.requireNonNull(currency, "currency");
        this.price = requireNonNegative(price);
    }

    public void setPrice(BigDecimal price) {
        this.price = requireNonNegative(price);
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice == null ? null : requireNonNegative(oldPrice);
    }

    private static BigDecimal requireNonNegative(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        return amount;
    }
}

