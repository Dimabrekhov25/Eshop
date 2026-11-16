package org.infrastructure.persistence.cart;

import cart.enums.CartStatus;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.infrastructure.persistence.accounts.UserEntity;
import org.infrastructure.persistence.common.BaseJpaEntity;

@Getter
@Entity
@Table(name = "carts")
public class CartEntity extends BaseJpaEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_carts_user_id"))
    private UserEntity user;

    @Column(name = "guest_id")
    private UUID guestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "promo_code_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_carts_promo_code_id"))
    private PromoCodeEntity promoCode;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", nullable = false, length = 32)
    private CartStatus status;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    private List<CartItemEntity> items = new ArrayList<>();

    protected CartEntity() {
    }
}

