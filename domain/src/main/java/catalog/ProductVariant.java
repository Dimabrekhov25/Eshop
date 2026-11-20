package catalog;

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
import lombok.Setter;

@Getter
@Entity
@Table(name = "product_variants", uniqueConstraints = {
    @UniqueConstraint(name = "uq_product_variants_sku", columnNames = {"sku"}),
    @UniqueConstraint(name = "uq_product_variants_product_id_color_id_size_id", columnNames = {"product_id", "color_id", "size_id"})
})
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_variants_product_id"))
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "color_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_variants_color_id"))
    private Color color;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "size_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_variants_size_id"))
    private Size size;

    @Column(name = "sku", nullable = false, length = 128)
    @Setter
    private String sku;

    @Column(name = "is_active", nullable = false)
    @Setter
    private boolean isActive = true;

    protected ProductVariant() {
    }

    public ProductVariant(Product product, Color color, Size size, String sku) {
        this.product = product;
        this.color = color;
        this.size = size;
        this.sku = sku;
    }
}
