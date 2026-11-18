package catalog;

import catalog.enums.Gender;
import catalog.enums.Status;
import common.BaseEntity;
import jakarta.persistence.CascadeType;
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
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "products", uniqueConstraints = {
    @UniqueConstraint(name = "uq_products_slug", columnNames = {"slug"})
})
public class Product extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_products_category_id"))
    @Setter
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "collection_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_products_collection_id"))
    @Setter
    private Collection collection;

    @Column(name = "name", nullable = false, length = 200)
    @Setter
    private String name;

    @Column(name = "slug", nullable = false, length = 200)
    @Setter
    private String slug;

    @Column(name = "short_description", columnDefinition = "text")
    @Setter
    private String shortDescription;

    @Column(name = "description", columnDefinition = "text")
    @Setter
    private String description;

    @Column(name = "origin_country", length = 100)
    @Setter
    private String originCountry;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "gender", nullable = false, length = 32)
    @Setter
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", nullable = false, length = 32)
    @Setter
    private Status status;

    @Column(name = "fit", length = 200)
    @Setter
    private String fit;

    @Column(name = "fabric_weight_gsm", precision = 10, scale = 2)
    @Setter
    private BigDecimal fabricWeightGsm;

    @Column(name = "model_height_cm", precision = 10, scale = 2)
    @Setter
    private BigDecimal modelHeightCm;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "model_size_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_products_model_size_id"))
    @Setter
    private Size modelSize;

    @Column(name = "size_guide", columnDefinition = "text")
    @Setter
    private String sizeGuide;

    @Column(name = "care_instructions", columnDefinition = "text")
    @Setter
    private String careInstructions;

    @Column(name = "is_essential", nullable = false)
    @Setter
    private boolean isEssential;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY,
        cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderBy("sortOrder ASC, id ASC")
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductMaterial> materials = new ArrayList<>();

    protected Product() {
    }

    public Product(Category category, String name, String slug, Gender gender) {
        this(category, name, slug, null, null, gender, Status.DRAFT);
    }

    public Product(
            Category category,
            String name,
            String slug,
            String shortDescription,
            String originCountry,
            Gender gender,
            Status status) {
        this.category = Objects.requireNonNull(category, "category");
        this.name = name;
        this.slug = slug;
        this.shortDescription = shortDescription;
        this.originCountry = originCountry;
        this.gender = gender;
        this.status = status;
    }

    public List<ProductImage> getImages() {
        return List.copyOf(images);
    }

    public void addImage(ProductImage image) {
        Objects.requireNonNull(image, "image");
        if (!getId().equals(image.getProduct().getId())) {
            throw new IllegalArgumentException("Image must belong to this product");
        }
        if (images.stream().anyMatch(existing -> existing.getId().equals(image.getId()))) {
            throw new IllegalArgumentException("Image is already in the gallery");
        }
        images.add(image);
        markUpdated();
    }

    public void removeImage(UUID imageId) {
        Objects.requireNonNull(imageId, "imageId");
        if (images.removeIf(image -> imageId.equals(image.getId()))) {
            markUpdated();
        }
    }

    public List<ProductVariant> getVariants() {
        return List.copyOf(variants);
    }

    public List<ProductMaterial> getMaterials() {
        return List.copyOf(materials);
    }
}
