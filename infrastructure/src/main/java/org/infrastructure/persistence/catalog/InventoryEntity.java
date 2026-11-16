package org.infrastructure.persistence.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
@Entity
@Table(name = "inventory")
public class InventoryEntity {

    @Id
    @Column(name = "variant_id", nullable = false)
    private UUID variantId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_inventory_variant_id"))
    private ProductVariantEntity variant;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected InventoryEntity() {
    }
}

