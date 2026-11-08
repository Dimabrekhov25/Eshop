package cart;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CartOwnershipTest {
    @Test
    void rejectsMissingOrAmbiguousOwner() {
        assertThrows(IllegalArgumentException.class, () -> new Cart(null, null));
        assertThrows(IllegalArgumentException.class,
            () -> new Cart(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void assigningGuestCartToAccountReplacesOwnerWithoutReplacingCart() {
        Cart cart = Cart.forGuest(UUID.randomUUID());
        UUID cartId = cart.getId();
        UUID userId = UUID.randomUUID();

        cart.assignToUser(userId);

        assertEquals(cartId, cart.getId());
        assertEquals(userId, cart.getUserId());
        assertNull(cart.getGuestId());
    }

    @Test
    void failedAssignmentPreservesGuestOwnership() {
        UUID guestId = UUID.randomUUID();
        Cart cart = Cart.forGuest(guestId);

        assertThrows(NullPointerException.class, () -> cart.assignToUser(null));

        assertEquals(guestId, cart.getGuestId());
        assertNull(cart.getUserId());
    }
}
