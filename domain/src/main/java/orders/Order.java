package orders;

import accounts.User;
import cart.PromoCode;
import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import orders.enums.OrderStatus;
import orders.enums.ShippingMethod;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "orders", uniqueConstraints = {
    @UniqueConstraint(name = "uq_orders_number", columnNames = {"number"})
})
public class Order extends BaseEntity {

    @Column(name = "number", nullable = false, length = 64)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_orders_user_id"))
    @Setter
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "promo_code_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_orders_promo_code_id"))
    @Setter
    private PromoCode promoCode;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "recipient_name", nullable = false, length = 200)
    private String recipientName;

    @Column(name = "phone", nullable = false, length = 40)
    private String phone;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "city", nullable = false, length = 200)
    private String city;

    @Column(name = "postal_code", nullable = false, length = 32)
    private String postalCode;

    @Column(name = "address_line1", nullable = false, length = 500)
    private String addressLine1;

    @Column(name = "address_line2", length = 500)
    @Setter
    private String addressLine2;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "shipping_method", nullable = false, length = 32)
    private ShippingMethod shippingMethod;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", nullable = false, length = 32)
    @Setter
    private OrderStatus status = OrderStatus.AWAITING_PAYMENT;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "subtotal", nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotal;

    @Column(name = "discount_total", nullable = false, precision = 19, scale = 4)
    private BigDecimal discountTotal;

    @Column(name = "shipping_total", nullable = false, precision = 19, scale = 4)
    private BigDecimal shippingTotal;

    @Column(name = "tax_total", nullable = false, precision = 19, scale = 4)
    private BigDecimal taxTotal;

    @Column(name = "total", nullable = false, precision = 19, scale = 4)
    private BigDecimal total;

    @Column(name = "promo_code_snapshot", length = 64)
    @Setter
    private String promoCodeSnapshot;

    @Column(name = "carrier", length = 100)
    @Setter
    private String carrier;

    @Column(name = "tracking_number", length = 200)
    @Setter
    private String trackingNumber;

    @Column(name = "payment_expires_at", nullable = false)
    private Instant paymentExpiresAt;

    @Column(name = "shipped_at")
    @Setter
    private Instant shippedAt;

    @Column(name = "delivered_at")
    @Setter
    private Instant deliveredAt;

    @Column(name = "cancelled_at")
    @Setter
    private Instant cancelledAt;

    @Column(name = "expired_at")
    @Setter
    private Instant expiredAt;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    protected Order() {
    }

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

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public List<Payment> getPayments() {
        return List.copyOf(payments);
    }
}
