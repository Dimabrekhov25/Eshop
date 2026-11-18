package catalog;

import catalog.enums.Gender;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CatalogBehaviorTest {
    @Test
    void galleryPreservesExistingImageAndRejectsDuplicates() {
        Product product = product();
        ProductImage image = new ProductImage("/front.jpg", "Front", 0, product);

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
        ProductImage image = new ProductImage("/front.jpg", "Front", 0, product());

        assertThrows(IllegalArgumentException.class, () -> product.addImage(image));
        assertTrue(product.getImages().isEmpty());
    }

    @Test
    void rejectedStockCorrectionDoesNotChangeAvailableQuantity() {
        Inventory inventory = new Inventory(variant(), 3);

        assertThrows(IllegalArgumentException.class, () -> inventory.setStockQuantity(-1));
        assertEquals(3, inventory.getStockQuantity());

        inventory.setStockQuantity(0);
        assertEquals(0, inventory.getStockQuantity());
    }

    @Test
    void materialShareMustBePositiveAndCannotExceedOneHundredPercent() {
        ProductMaterial material = new ProductMaterial(
            product(), new Material("Cotton", "COTTON"), new BigDecimal("95"));

        assertThrows(IllegalArgumentException.class, () -> material.setPercentage(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
            () -> material.setPercentage(new BigDecimal("100.01")));
        assertEquals(new BigDecimal("95"), material.getPercentage());

        material.setPercentage(new BigDecimal("100"));
        assertEquals(new BigDecimal("100"), material.getPercentage());
    }

    private Product product() {
        return new Product(new Category("Clothing", "clothing"),
            "Essential T-Shirt", "essential-t-shirt", Gender.UNISEX);
    }

    private ProductVariant variant() {
        return new ProductVariant(product(), new Color("Black", "BLACK"), new Size("M", "Medium", 1), "SKU");
    }
}
