package cart;

import cart.enums.DiscountType;
import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "promo_codes", uniqueConstraints = {
    @UniqueConstraint(name = "uq_promo_codes_code", columnNames = {"code"})
})
public class PromoCode extends BaseEntity {

    @Column(name = "code", nullable = false, length = 64)
    @Setter
    private String code;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "discount_type", nullable = false, length = 32)
    @Setter
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 19, scale = 4)
    @Setter
    private BigDecimal discountValue;

    @Column(name = "min_order_amount", precision = 19, scale = 4)
    @Setter
    private BigDecimal minOrderAmount;

    @Column(name = "usage_limit")
    @Setter
    private Integer usageLimit;

    @Column(name = "starts_at", nullable = false)
    @Setter
    private Instant startsAt;

    @Column(name = "expires_at")
    @Setter
    private Instant expiresAt;

    @Column(name = "is_active", nullable = false)
    @Setter
    private boolean isActive = true;

    protected PromoCode() {
    }

    public PromoCode(String code, DiscountType discountType, BigDecimal discountValue, Instant startsAt) {
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.startsAt = startsAt;
    }
}
