package cart;

import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import cart.enums.DiscountType;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class PromoCode extends BaseEntity {
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount; // Optional.
    private Integer usageLimit; // Optional.
    private Instant startsAt;
    private Instant expiresAt; // Optional.
    private boolean isActive = true;

    public PromoCode(String code, DiscountType discountType, BigDecimal discountValue, Instant startsAt) {
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.startsAt = startsAt;
    }
}

