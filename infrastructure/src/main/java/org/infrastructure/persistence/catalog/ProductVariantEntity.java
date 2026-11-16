package org.infrastructure.persistence.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import org.infrastructure.persistence.common.BaseJpaEntity;

@Getter
@Entity
@Table(name = "product_variants", uniqueConstraints = {
    @UniqueConstraint(name = "uq_product_variants_sku", columnNames = {"sku"}),
    @UniqueConstraint(name = "uq_product_variants_product_id_color_id_size_id", columnNames = {"product_id", "color_id", "size_id"})
})
public class ProductVariantEntity extends BaseJpaEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_variants_product_id"))
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "color_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_variants_color_id"))
    private ColorEntity color;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "size_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_variants_size_id"))
    private SizeEntity size;

    @Column(name = "sku", nullable = false, length = 128)
    private String sku;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    protected ProductVariantEntity() {
    }
}

