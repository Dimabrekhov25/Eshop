package discovery;

import catalog.ProductVariant;
import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "stock_alerts")
public class StockAlert extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_stock_alerts_variant_id"))
    private ProductVariant variant;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "notified_at")
    @Setter
    private Instant notifiedAt;

    protected StockAlert() {
    }

    public StockAlert(ProductVariant variant, String email) {
        this.variant = variant;
        this.email = email;
    }
}
