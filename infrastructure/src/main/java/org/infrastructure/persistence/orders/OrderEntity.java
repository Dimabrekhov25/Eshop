package org.infrastructure.persistence.orders;

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
import lombok.Getter;
import orders.enums.OrderStatus;
import orders.enums.ShippingMethod;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.infrastructure.persistence.accounts.UserEntity;
import org.infrastructure.persistence.cart.PromoCodeEntity;
import org.infrastructure.persistence.common.BaseJpaEntity;

@Getter
@Entity
@Table(name = "orders", uniqueConstraints = {
    @UniqueConstraint(name = "uq_orders_number", columnNames = {"number"})
})
public class OrderEntity extends BaseJpaEntity {

    @Column(name = "number", nullable = false, length = 64)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_orders_user_id"))
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "promo_code_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_orders_promo_code_id"))
    private PromoCodeEntity promoCode;

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
    private String addressLine2;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "shipping_method", nullable = false, length = 32)
    private ShippingMethod shippingMethod;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", nullable = false, length = 32)
    private OrderStatus status;

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
    private String promoCodeSnapshot;

    @Column(name = "carrier", length = 100)
    private String carrier;

    @Column(name = "tracking_number", length = 200)
    private String trackingNumber;

    @Column(name = "payment_expires_at", nullable = false)
    private Instant paymentExpiresAt;

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItemEntity> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<PaymentEntity> payments = new ArrayList<>();

    protected OrderEntity() {
    }
}

