package org.infrastructure.persistence.catalog;

import catalog.enums.Gender;
import catalog.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.infrastructure.persistence.common.BaseJpaEntity;

@Getter
@Entity
@Table(name = "products", uniqueConstraints = {
    @UniqueConstraint(name = "uq_products_slug", columnNames = {"slug"})
})
public class ProductEntity extends BaseJpaEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_products_category_id"))
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "collection_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_products_collection_id"))
    private CollectionEntity collection;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "slug", nullable = false, length = 200)
    private String slug;

    @Column(name = "short_description", columnDefinition = "text")
    private String shortDescription;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "origin_country", length = 100)
    private String originCountry;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "gender", nullable = false, length = 32)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", nullable = false, length = 32)
    private Status status;

    @Column(name = "fit", length = 200)
    private String fit;

    @Column(name = "fabric_weight_gsm", precision = 10, scale = 2)
    private BigDecimal fabricWeightGsm;

    @Column(name = "model_height_cm", precision = 10, scale = 2)
    private BigDecimal modelHeightCm;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "model_size_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_products_model_size_id"))
    private SizeEntity modelSize;

    @Column(name = "size_guide", columnDefinition = "text")
    private String sizeGuide;

    @Column(name = "care_instructions", columnDefinition = "text")
    private String careInstructions;

    @Column(name = "is_essential", nullable = false)
    private boolean isEssential;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductVariantEntity> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC, id ASC")
    private List<ProductImageEntity> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductMaterialEntity> materials = new ArrayList<>();

    protected ProductEntity() {
    }
}

