package org.infrastructure.persistence.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import org.infrastructure.persistence.common.BaseJpaEntity;

@Getter
@Entity
@Table(name = "product_images")
public class ProductImageEntity extends BaseJpaEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_product_images_product_id"))
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "color_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_product_images_color_id"))
    private ColorEntity color;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @Column(name = "alt_text", columnDefinition = "text")
    private String altText;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected ProductImageEntity() {
    }
}

