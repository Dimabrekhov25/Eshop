package org.infrastructure.persistence.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "product_materials")
public class ProductMaterialEntity {

    @EmbeddedId
    private ProductMaterialId id;

    @Column(name = "percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_materials_product_id"))
    private ProductEntity product;

    @MapsId("materialId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_materials_material_id"))
    private MaterialEntity material;

    protected ProductMaterialEntity() {
    }

    public ProductMaterialEntity(ProductEntity product, MaterialEntity material, BigDecimal percentage) {
        this.product = Objects.requireNonNull(product, "product");
        this.material = Objects.requireNonNull(material, "material");
        this.id = new ProductMaterialId(product.getId(), material.getId());
        this.percentage = Objects.requireNonNull(percentage, "percentage");
    }
}

