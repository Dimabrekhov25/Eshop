package orders;

import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import orders.enums.OrderStatus;
import orders.enums.ShippingMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Order extends BaseEntity {
    private final String number;

    @Setter
    private UUID userId; // Optional; null for guest checkout.

    @Setter
    private UUID promoCodeId; // Optional.

    private final String email;
    private final String recipientName;
    private final String phone;
    private final String countryCode;
    private final String city;
    private final String postalCode;
    private final String addressLine1;

    @Setter
    private String addressLine2; // Optional.

    private final ShippingMethod shippingMethod;

    @Setter
    private OrderStatus status = OrderStatus.AWAITING_PAYMENT;

    private final String currency;
    private final BigDecimal subtotal;
    private final BigDecimal discountTotal;
    private final BigDecimal shippingTotal;
    private final BigDecimal taxTotal;
    private final BigDecimal total;

    @Setter
    private String promoCodeSnapshot; // Optional.

    @Setter
    private String carrier; // Optional.

    @Setter
    private String trackingNumber; // Optional.

    private final Instant paymentExpiresAt;

    @Setter
    private Instant shippedAt; // Optional until shipped.

    @Setter
    private Instant deliveredAt; // Optional until delivered.

    @Setter
    private Instant cancelledAt; // Optional until cancelled.

    @Setter
    private Instant expiredAt; // Optional until expired.

    public Order(
            String number,
            String email,
            String recipientName,
            String phone,
            String countryCode,
            String city,
            String postalCode,
            String addressLine1,
            ShippingMethod shippingMethod,
            String currency,
            BigDecimal subtotal,
            BigDecimal discountTotal,
            BigDecimal shippingTotal,
            BigDecimal taxTotal,
            BigDecimal total,
            Instant paymentExpiresAt) {
        this.number = number;
        this.email = email;
        this.recipientName = recipientName;
        this.phone = phone;
        this.countryCode = countryCode;
        this.city = city;
        this.postalCode = postalCode;
        this.addressLine1 = addressLine1;
        this.shippingMethod = shippingMethod;
        this.currency = currency;
        this.subtotal = subtotal;
        this.discountTotal = discountTotal;
        this.shippingTotal = shippingTotal;
        this.taxTotal = taxTotal;
        this.total = total;
        this.paymentExpiresAt = Objects.requireNonNull(paymentExpiresAt, "paymentExpiresAt");
    }
}

