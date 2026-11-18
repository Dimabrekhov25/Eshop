package catalog;

import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "product_images")
public class ProductImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_images_product_id"))
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "color_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_product_images_color_id"))
    @Setter
    private Color color;

    @Column(name = "url", nullable = false, length = 2048)
    @Setter
    private String url;

    @Column(name = "alt_text", columnDefinition = "text")
    @Setter
    private String altText;

    @Column(name = "sort_order", nullable = false)
    @Setter
    private int sortOrder;

    protected ProductImage() {
    }

    public ProductImage(String url, String altText, int sortOrder, Product product) {
        this.product = Objects.requireNonNull(product, "product");
        this.url = url;
        this.altText = altText;
        this.sortOrder = sortOrder;
    }
}
