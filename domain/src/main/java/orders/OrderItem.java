package orders;

import common.BaseEntity;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class OrderItem extends BaseEntity {
    private final UUID orderId;
    private final UUID variantId; // Optional reference; snapshots survive catalog changes.
    private final String skuSnapshot;
    private final String productNameSnapshot;
    private final String colorSnapshot;
    private final String sizeSnapshot;
    private final BigDecimal unitPrice;
    private final int quantity;
    private final BigDecimal discountTotal;
    private final BigDecimal taxTotal;
    private final BigDecimal total;

    public OrderItem(
            UUID orderId,
            UUID variantId,
            String skuSnapshot,
            String productNameSnapshot,
            String colorSnapshot,
            String sizeSnapshot,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal discountTotal,
            BigDecimal taxTotal,
            BigDecimal total) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Order item quantity must be greater than 0");
        }
        this.orderId = orderId;
        this.variantId = variantId;
        this.skuSnapshot = skuSnapshot;
        this.productNameSnapshot = productNameSnapshot;
        this.colorSnapshot = colorSnapshot;
        this.sizeSnapshot = sizeSnapshot;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discountTotal = discountTotal;
        this.taxTotal = taxTotal;
        this.total = total;
    }
}

