package catalog;
import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class ProductImage extends BaseEntity {
    @Setter
    private String url;
    @Setter
    private String altText;
    @Setter
    private int sortOrder;
    
    private final UUID productId;

    @Setter
    private UUID colorId; // Optional; null means shared by all product colors.
    
    public ProductImage(String url, String altText, int sortOrder, UUID productId) {
        this.productId = productId;
        this.url = url;
        this.altText = altText;
        this.sortOrder = sortOrder;
    }
}
