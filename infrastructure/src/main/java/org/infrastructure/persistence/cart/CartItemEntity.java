package org.infrastructure.persistence.cart;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import org.infrastructure.persistence.catalog.ProductVariantEntity;
import org.infrastructure.persistence.common.BaseJpaEntity;

@Getter
@Entity
@Table(name = "cart_items", uniqueConstraints = {
    @UniqueConstraint(name = "uq_cart_items_cart_id_variant_id", columnNames = {"cart_id", "variant_id"})
})
public class CartItemEntity extends BaseJpaEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_cart_items_cart_id"))
    private CartEntity cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_cart_items_variant_id"))
    private ProductVariantEntity variant;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    protected CartItemEntity() {
    }
}

