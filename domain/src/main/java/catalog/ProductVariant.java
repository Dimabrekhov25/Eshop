package catalog;

import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ProductVariant extends BaseEntity {
    private final UUID productId;
    private final UUID colorId;
    private final UUID sizeId;
    private String sku;
    private boolean isActive = true;

    public ProductVariant(UUID productId, UUID colorId, UUID sizeId, String sku) {
        this.productId = productId;
        this.colorId = colorId;
        this.sizeId = sizeId;
        this.sku = sku;
    }
}

