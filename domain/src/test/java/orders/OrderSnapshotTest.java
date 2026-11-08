package orders;

import catalog.Product;
import catalog.ProductVariant;
import catalog.ProductVariantPrice;
import catalog.enums.Gender;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderSnapshotTest {
    @Test
    void catalogChangesDoNotChangePurchasedNameOrPrice() {
        Product product = new Product(UUID.randomUUID(), "Original name", "original-name", Gender.UNISEX);
        ProductVariant variant = new ProductVariant(product.getId(), UUID.randomUUID(), UUID.randomUUID(), "TEE-BLACK-M");
        ProductVariantPrice price = new ProductVariantPrice(variant.getId(), "EUR", new BigDecimal("50.00"));
        OrderItem item = new OrderItem(
            UUID.randomUUID(), variant.getId(), variant.getSku(), product.getName(),
            "Black", "M", price.getPrice(), 1, BigDecimal.ZERO, BigDecimal.ZERO, price.getPrice()
        );

        product.setName("New catalog name");
        price.setPrice(new BigDecimal("75.00"));
        variant.setSku("NEW-SKU");

        assertEquals("Original name", item.getProductNameSnapshot());
        assertEquals("TEE-BLACK-M", item.getSkuSnapshot());
        assertEquals(new BigDecimal("50.00"), item.getUnitPrice());
        assertEquals(new BigDecimal("50.00"), item.getTotal());
    }
}
