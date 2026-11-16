package org.infrastructure.persistence.discovery;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import org.infrastructure.persistence.catalog.ProductVariantEntity;
import org.infrastructure.persistence.common.BaseJpaEntity;

@Getter
@Entity
@Table(name = "stock_alerts")
public class StockAlertEntity extends BaseJpaEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_stock_alerts_variant_id"))
    private ProductVariantEntity variant;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "notified_at")
    private Instant notifiedAt;

    protected StockAlertEntity() {
    }
}

