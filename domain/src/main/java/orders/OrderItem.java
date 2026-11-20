package orders;

import catalog.ProductVariant;
import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_order_items_order_id"))
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "variant_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_order_items_variant_id"))
    private ProductVariant variant;

    @Column(name = "sku_snapshot", nullable = false, length = 128)
    private String skuSnapshot;

    @Column(name = "product_name_snapshot", nullable = false, length = 200)
    private String productNameSnapshot;

    @Column(name = "color_snapshot", nullable = false, length = 200)
    private String colorSnapshot;

    @Column(name = "size_snapshot", nullable = false, length = 200)
    private String sizeSnapshot;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "discount_total", nullable = false, precision = 19, scale = 4)
    private BigDecimal discountTotal;

    @Column(name = "tax_total", nullable = false, precision = 19, scale = 4)
    private BigDecimal taxTotal;

    @Column(name = "total", nullable = false, precision = 19, scale = 4)
    private BigDecimal total;

    protected OrderItem() {
    }

    public OrderItem(
            Order order,
            ProductVariant variant,
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
        this.order = order;
        this.variant = variant;
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
