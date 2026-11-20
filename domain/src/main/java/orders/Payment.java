package orders;

import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import orders.enums.PaymentStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "payments", uniqueConstraints = {
    @UniqueConstraint(name = "uq_payments_provider_provider_payment_id", columnNames = {"provider", "provider_payment_id"})
})
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_payments_order_id"))
    private Order order;

    @Column(name = "provider", nullable = false, length = 100)
    @Setter
    private String provider;

    @Column(name = "provider_payment_id", length = 255)
    @Setter
    private String providerPaymentId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    @Setter
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @Setter
    private String currency;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", nullable = false, length = 32)
    @Setter
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "paid_at")
    @Setter
    private Instant paidAt;

    protected Payment() {
    }

    public Payment(Order order, String provider, BigDecimal amount, String currency) {
        this.order = order;
        this.provider = provider;
        this.amount = amount;
        this.currency = currency;
    }
}
