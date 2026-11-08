package catalog;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
public class ProductMaterial {
    private final UUID productId;
    private final UUID materialId;
    private BigDecimal percentage;

    public ProductMaterial(UUID productId, UUID materialId, BigDecimal percentage) {
        this.productId = Objects.requireNonNull(productId, "productId");
        this.materialId = Objects.requireNonNull(materialId, "materialId");
        this.percentage = requirePercentage(percentage);
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = requirePercentage(percentage);
    }

    private static BigDecimal requirePercentage(BigDecimal percentage) {
        Objects.requireNonNull(percentage, "percentage");
        if (percentage.signum() <= 0 || percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Material percentage must be greater than 0 and at most 100");
        }
        return percentage;
    }
}

