package catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
@Entity
@Table(name = "product_variant_prices")
public class ProductVariantPrice {

    @Id
    @Column(name = "variant_id", nullable = false)
    private UUID variantId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_variant_prices_variant_id"))
    private ProductVariant variant;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "price", nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(name = "old_price", precision = 19, scale = 4)
    private BigDecimal oldPrice;

    protected ProductVariantPrice() {
    }

    public ProductVariantPrice(ProductVariant variant, String currency, BigDecimal price) {
        this.variant = Objects.requireNonNull(variant, "variant");
        this.variantId = variant.getId();
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
