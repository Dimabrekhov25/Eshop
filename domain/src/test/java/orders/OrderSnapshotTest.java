package orders;

import catalog.Category;
import catalog.Color;
import catalog.Product;
import catalog.ProductVariant;
import catalog.ProductVariantPrice;
import catalog.Size;
import catalog.enums.Gender;
import orders.enums.ShippingMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderSnapshotTest {
    @Test
    void catalogChangesDoNotChangePurchasedNameOrPrice() {
        Product product = new Product(new Category("Clothing", "clothing"),
            "Original name", "original-name", Gender.UNISEX);
        ProductVariant variant = new ProductVariant(product, new Color("Black", "BLACK"),
            new Size("M", "Medium", 1), "TEE-BLACK-M");
        ProductVariantPrice price = new ProductVariantPrice(variant, "EUR", new BigDecimal("50.00"));
        Order order = new Order("ORDER-1", "guest@example.com", "Guest", "+361234567", "HU", "Budapest",
            "1000", "Address", ShippingMethod.COURIER, "EUR", price.getPrice(), BigDecimal.ZERO,
            BigDecimal.ZERO, BigDecimal.ZERO, price.getPrice(), Instant.now().plusSeconds(900));
        OrderItem item = new OrderItem(
            order, variant, variant.getSku(), product.getName(),
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
