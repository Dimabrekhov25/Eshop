package cart;

import accounts.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CartOwnershipTest {
    @Test
    void rejectsMissingOrAmbiguousOwner() {
        assertThrows(IllegalArgumentException.class, () -> new Cart(null, null));
        assertThrows(IllegalArgumentException.class,
            () -> new Cart(new User("user@example.com", "hash"), UUID.randomUUID()));
    }

    @Test
    void assigningGuestCartToAccountReplacesOwnerWithoutReplacingCart() {
        Cart cart = Cart.forGuest(UUID.randomUUID());
        UUID cartId = cart.getId();
        User user = new User("user@example.com", "hash");

        cart.assignToUser(user);

        assertEquals(cartId, cart.getId());
        assertSame(user, cart.getUser());
        assertNull(cart.getGuestId());
    }

    @Test
    void failedAssignmentPreservesGuestOwnership() {
        UUID guestId = UUID.randomUUID();
        Cart cart = Cart.forGuest(guestId);

        assertThrows(NullPointerException.class, () -> cart.assignToUser(null));

        assertEquals(guestId, cart.getGuestId());
        assertNull(cart.getUser());
    }
}
