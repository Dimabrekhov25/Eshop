package catalog;

import catalog.enums.Gender;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CatalogBehaviorTest {
    @Test
    void galleryPreservesExistingImageAndRejectsDuplicates() {
        Product product = product();
        ProductImage image = new ProductImage("/front.jpg", "Front", 0, product.getId());

        product.addImage(image);

        assertSame(image, product.getImages().get(0));
        assertThrows(IllegalArgumentException.class, () -> product.addImage(image));
        assertThrows(UnsupportedOperationException.class, () -> product.getImages().clear());
        assertEquals(1, product.getImages().size());

        product.removeImage(image.getId());
        assertTrue(product.getImages().isEmpty());
    }

    @Test
    void galleryRejectsImageOwnedByAnotherProduct() {
        Product product = product();
        ProductImage image = new ProductImage("/front.jpg", "Front", 0, UUID.randomUUID());

        assertThrows(IllegalArgumentException.class, () -> product.addImage(image));
        assertTrue(product.getImages().isEmpty());
    }

    @Test
    void rejectedStockCorrectionDoesNotChangeAvailableQuantity() {
        Inventory inventory = new Inventory(UUID.randomUUID(), 3);

        assertThrows(IllegalArgumentException.class, () -> inventory.setStockQuantity(-1));
        assertEquals(3, inventory.getStockQuantity());

        inventory.setStockQuantity(0);
        assertEquals(0, inventory.getStockQuantity());
    }

    @Test
    void materialShareMustBePositiveAndCannotExceedOneHundredPercent() {
        UUID productId = UUID.randomUUID();
        UUID materialId = UUID.randomUUID();
        ProductMaterial material = new ProductMaterial(productId, materialId, new BigDecimal("95"));

        assertThrows(IllegalArgumentException.class, () -> material.setPercentage(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
            () -> material.setPercentage(new BigDecimal("100.01")));
        assertEquals(new BigDecimal("95"), material.getPercentage());

        material.setPercentage(new BigDecimal("100"));
        assertEquals(new BigDecimal("100"), material.getPercentage());
    }

    private Product product() {
        return new Product(UUID.randomUUID(), "Essential T-Shirt", "essential-t-shirt", Gender.UNISEX);
    }
}
