package cart;

import accounts.User;
import cart.enums.CartStatus;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "carts")
public class Cart extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_carts_user_id"))
    private User user;

    @Column(name = "guest_id")
    private UUID guestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "promo_code_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_carts_promo_code_id"))
    @Setter
    private PromoCode promoCode;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", nullable = false, length = 32)
    @Setter
    private CartStatus status = CartStatus.ACTIVE;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    private List<CartItem> items = new ArrayList<>();

    protected Cart() {
    }

    public Cart(User user, UUID guestId) {
        if ((user == null) == (guestId == null)) {
            throw new IllegalArgumentException("Cart must have either user or guestId, but not both");
        }
        this.user = user;
        this.guestId = guestId;
    }

    public static Cart forUser(User user) {
        return new Cart(Objects.requireNonNull(user, "user"), null);
    }

    public static Cart forGuest(UUID guestId) {
        return new Cart(null, Objects.requireNonNull(guestId, "guestId"));
    }

    public void assignToUser(User user) {
        this.user = Objects.requireNonNull(user, "user");
        this.guestId = null;
        markUpdated();
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }
}
