package cart;

import catalog.ProductVariant;
import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

@Getter
@Entity
@Table(name = "cart_items", uniqueConstraints = {
    @UniqueConstraint(name = "uq_cart_items_cart_id_variant_id", columnNames = {"cart_id", "variant_id"})
})
public class CartItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_cart_items_cart_id"))
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_cart_items_variant_id"))
    private ProductVariant variant;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    protected CartItem() {
    }

    public CartItem(Cart cart, ProductVariant variant, int quantity) {
        this.cart = cart;
        this.variant = variant;
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
