package catalog;

import catalog.enums.Gender;
import catalog.enums.Status;
import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Product extends BaseEntity {
    @Setter
    private UUID categoryId;

    @Setter
    private UUID collectionId; // Optional.

    @Setter
    private String name;

    @Setter
    private String slug;

    @Setter
    private String shortDescription;

    @Setter
    private String description; // Optional; full description.

    @Setter
    private String originCountry;

    @Setter
    private Gender gender;

    @Setter
    private Status status;

    @Setter
    private String fit; // Optional.

    @Setter
    private BigDecimal fabricWeightGsm; // Optional.

    @Setter
    private BigDecimal modelHeightCm; // Optional.

    @Setter
    private UUID modelSizeId; // Optional.

    @Setter
    private String sizeGuide; // Optional.

    @Setter
    private String careInstructions; // Optional.

    @Setter
    private boolean isEssential;

    private final List<ProductImage> images = new ArrayList<>();

    public Product(UUID categoryId, String name, String slug, Gender gender) {
        this(name, slug, null, null, gender, Status.DRAFT);
        this.categoryId = Objects.requireNonNull(categoryId, "categoryId");
    }
    
    
    public Product (
            String name,
            String slug,
            String shortDescription,
            String originCountry, 
            Gender gender,
            Status status){
        super();
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
        if (!getId().equals(image.getProductId())) {
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
    
}
