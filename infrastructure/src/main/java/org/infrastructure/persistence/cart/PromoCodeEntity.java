package org.infrastructure.persistence.cart;

import cart.enums.DiscountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.infrastructure.persistence.common.BaseJpaEntity;

@Getter
@Entity
@Table(name = "promo_codes", uniqueConstraints = {
    @UniqueConstraint(name = "uq_promo_codes_code", columnNames = {"code"})
})
public class PromoCodeEntity extends BaseJpaEntity {

    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "discount_type", nullable = false, length = 32)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal discountValue;

    @Column(name = "min_order_amount", precision = 19, scale = 4)
    private BigDecimal minOrderAmount;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    protected PromoCodeEntity() {
    }
}

