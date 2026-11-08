package orders;

import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import orders.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class Payment extends BaseEntity {
    private final UUID orderId;
    private String provider;
    private String providerPaymentId; // Optional.
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status = PaymentStatus.PENDING;
    private Instant paidAt; // Optional.

    public Payment(UUID orderId, String provider, BigDecimal amount, String currency) {
        this.orderId = orderId;
        this.provider = provider;
        this.amount = amount;
        this.currency = currency;
    }
}

