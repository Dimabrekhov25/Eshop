package org.infrastructure.persistence;

import catalog.enums.Gender;
import catalog.enums.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.infrastructure.persistence.accounts.UserProfileEntity;
import org.infrastructure.persistence.accounts.UserEntity;
import org.infrastructure.persistence.catalog.CategoryEntity;
import org.infrastructure.persistence.catalog.ProductEntity;
import org.infrastructure.persistence.catalog.MaterialEntity;
import org.infrastructure.persistence.catalog.ProductMaterialEntity;
import org.infrastructure.persistence.catalog.ProductMaterialId;
import org.infrastructure.persistence.catalog.ProductVariantPriceEntity;
import org.infrastructure.persistence.orders.OrderItemEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Runs only against an explicitly supplied, dedicated PostgreSQL test database. */
@EnabledIfEnvironmentVariable(named = "ESHOP_SCHEMA_TEST_URL", matches = ".+")
@SpringBootTest(classes = PersistenceSchemaTest.TestApplication.class, properties = {
    "spring.jpa.hibernate.ddl-auto=validate",
    "spring.jpa.open-in-view=false",
    "spring.flyway.enabled=true"
})
@Transactional
class PersistenceSchemaTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(PersistenceConfiguration.class)
    static class TestApplication {
    }

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        String url = System.getenv("ESHOP_SCHEMA_TEST_URL");
        if (!url.matches("jdbc:postgresql://[^/]+/[^?]*_test(?:\\?.*)?")) {
            throw new IllegalArgumentException("Schema tests require a dedicated database ending in _test");
        }
        registry.add("spring.datasource.url", () -> url);
        registry.add("spring.datasource.username",
            () -> System.getenv().getOrDefault("ESHOP_SCHEMA_TEST_USER", "postgres"));
        registry.add("spring.datasource.password",
            () -> System.getenv().getOrDefault("ESHOP_SCHEMA_TEST_PASSWORD", ""));
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired EntityManager entityManager;
    @Autowired EntityManagerFactory entityManagerFactory;
    @Autowired Flyway flyway;

    @Test
    void migratesEveryTableAndValidatesAllMappings() {
        Set<String> expected = Set.of("users", "user_profiles", "admin_profiles", "categories",
            "collections", "colors", "sizes", "materials", "products", "product_materials",
            "product_variants", "product_variant_prices", "inventory", "product_images",
            "promo_codes", "carts", "cart_items", "orders", "order_items", "payments",
            "wishlist_items", "reviews", "stock_alerts", "pages", "journal_articles");
        Set<String> actual = new HashSet<>(jdbc.queryForList(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'", String.class));
        actual.remove("flyway_schema_history");
        assertEquals(expected, actual);
        assertEquals(25, entityManagerFactory.getMetamodel().getEntities().size());
        assertEquals(0, flyway.migrate().migrationsExecuted);
    }

    @Test
    void readsPersistedIdentifiersTimestampsAndDomainEnums() {
        Catalog catalog = catalog();
        ProductEntity product = entityManager.find(ProductEntity.class, catalog.productId());
        assertEquals(catalog.productId(), product.getId());
        assertNotNull(product.getCreatedAt());
        assertEquals(Gender.UNISEX, product.getGender());
        assertEquals(Status.DRAFT, product.getStatus());
        assertEquals(catalog.categoryId(), product.getCategory().getId());
        assertEquals("category", product.getCategory().getSlug());
        assertEquals("category", entityManager.find(CategoryEntity.class, catalog.categoryId()).getSlug());
    }

    @Test
    void readsSharedAndCompositePrimaryKeysAndDecimalPrices() {
        Catalog catalog = catalog();
        UUID userId = user();
        jdbc.update("INSERT INTO user_profiles (user_id, name) VALUES (?, ?)", userId, "Customer");
        jdbc.update("INSERT INTO product_materials (product_id, material_id, percentage) VALUES (?, ?, ?)",
            catalog.productId(), catalog.materialId(), new BigDecimal("95.50"));
        jdbc.update("INSERT INTO product_variant_prices (variant_id, currency, price) VALUES (?, ?, ?)",
            catalog.variantId(), "EUR", new BigDecimal("12.3456"));

        assertEquals("Customer", entityManager.find(UserProfileEntity.class, userId).getName());
        assertEquals(userId, entityManager.find(UserProfileEntity.class, userId).getUser().getId());
        ProductMaterialEntity composition = entityManager.find(ProductMaterialEntity.class,
            new ProductMaterialId(catalog.productId(), catalog.materialId()));
        assertEquals(new BigDecimal("95.50"), composition.getPercentage());
        assertEquals(catalog.productId(), composition.getId().getProductId());
        assertEquals(catalog.productId(), composition.getProduct().getId());
        assertEquals(catalog.materialId(), composition.getMaterial().getId());
        assertEquals(new BigDecimal("12.3456"), entityManager
            .find(ProductVariantPriceEntity.class, catalog.variantId()).getPrice());
        assertEquals(catalog.variantId(), entityManager
            .find(ProductVariantPriceEntity.class, catalog.variantId()).getVariant().getId());
    }

    @Test
    void navigatesInverseCollectionsAndOrdersGalleryImages() {
        Catalog catalog = catalog();
        UUID childId = UUID.randomUUID();
        baseRow("categories", childId, "name, slug, parent_id", "Child", "child", catalog.categoryId());
        baseRow("product_images", UUID.randomUUID(), "product_id, url, sort_order",
            catalog.productId(), "https://example.com/second.jpg", 2);
        baseRow("product_images", UUID.randomUUID(), "product_id, url, sort_order",
            catalog.productId(), "https://example.com/first.jpg", 1);
        jdbc.update("INSERT INTO product_materials (product_id, material_id, percentage) VALUES (?, ?, ?)",
            catalog.productId(), catalog.materialId(), new BigDecimal("100.00"));

        CategoryEntity category = entityManager.find(CategoryEntity.class, catalog.categoryId());
        assertEquals(childId, category.getChildren().get(0).getId());
        assertEquals(catalog.categoryId(), category.getChildren().get(0).getParent().getId());
        ProductEntity product = category.getProducts().get(0);
        assertEquals(catalog.productId(), product.getId());
        assertEquals(catalog.variantId(), product.getVariants().get(0).getId());
        assertEquals("https://example.com/first.jpg", product.getImages().get(0).getUrl());
        assertEquals("Cotton", product.getMaterials().get(0).getMaterial().getName());
        assertEquals(catalog.productId(), entityManager.find(MaterialEntity.class, catalog.materialId())
            .getProductMaterials().get(0).getProduct().getId());
    }

    @Test
    void persistsCompositeLinkUsingMappedAssociations() {
        Catalog catalog = catalog();
        ProductMaterialEntity link = new ProductMaterialEntity(
            entityManager.getReference(ProductEntity.class, catalog.productId()),
            entityManager.getReference(MaterialEntity.class, catalog.materialId()),
            new BigDecimal("100.00"));
        entityManager.persist(link);
        entityManager.flush();
        entityManager.clear();

        ProductMaterialEntity loaded = entityManager.find(ProductMaterialEntity.class,
            new ProductMaterialId(catalog.productId(), catalog.materialId()));
        assertEquals(new BigDecimal("100.00"), loaded.getPercentage());
        assertEquals("Product", loaded.getProduct().getName());
        assertEquals("Cotton", loaded.getMaterial().getName());
    }

    @Test
    void derivesSharedPrimaryKeyFromOneToOneAssociation() {
        UUID userId = user();
        UserProfileEntity profile = new UserProfileEntity(
            entityManager.getReference(UserEntity.class, userId), "Customer", null);
        entityManager.persist(profile);
        entityManager.flush();
        assertEquals(userId, profile.getUserId());
        entityManager.clear();
        assertEquals(userId, entityManager.find(UserProfileEntity.class, userId).getUser().getId());
    }

    @Test
    void rejectsDuplicateCategorySlugs() {
        baseRow("categories", UUID.randomUUID(), "name, slug", "First", "same-slug");
        assertThrows(DataIntegrityViolationException.class,
            () -> baseRow("categories", UUID.randomUUID(), "name, slug", "Second", "same-slug"));
    }

    @Test
    void rejectsReferencesToMissingCategories() {
        assertThrows(DataIntegrityViolationException.class, () -> baseRow("products", UUID.randomUUID(),
            "category_id, name, slug, gender, status", UUID.randomUUID(), "Product", "product", "UNISEX", "DRAFT"));
    }

    @Test
    void cartCannotHaveBothUserAndGuestOwners() {
        UUID userId = user();
        assertThrows(DataIntegrityViolationException.class, () -> baseRow("carts", UUID.randomUUID(),
            "user_id, guest_id, status", userId, UUID.randomUUID(), "ACTIVE"));
    }

    @Test
    void inventoryCannotBecomeNegative() {
        Catalog catalog = catalog();
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update(
            "INSERT INTO inventory (variant_id, stock_quantity, updated_at) VALUES (?, ?, ?)",
            catalog.variantId(), -1, Timestamp.from(Instant.now())));
    }

    @Test
    void pendingStockAlertsAreUniqueIgnoringEmailCase() {
        Catalog catalog = catalog();
        baseRow("stock_alerts", UUID.randomUUID(), "variant_id, email", catalog.variantId(), "User@example.com");
        assertThrows(DataIntegrityViolationException.class, () -> baseRow("stock_alerts", UUID.randomUUID(),
            "variant_id, email", catalog.variantId(), "user@example.com"));
    }

    @Test
    void historicalStockAlertAllowsAnotherSubscription() {
        Catalog catalog = catalog();
        baseRow("stock_alerts", UUID.randomUUID(), "variant_id, email, notified_at", catalog.variantId(),
            "user@example.com", Timestamp.from(Instant.now()));
        baseRow("stock_alerts", UUID.randomUUID(), "variant_id, email", catalog.variantId(), "user@example.com");
        assertEquals(2, jdbc.queryForObject("SELECT count(*) FROM stock_alerts WHERE variant_id = ?",
            Integer.class, catalog.variantId()));
    }

    @Test
    void purchasedSnapshotsSurvivePhysicalVariantDeletion() {
        Catalog catalog = catalog();
        UUID orderId = UUID.randomUUID();
        baseRow("orders", orderId,
            "number, email, recipient_name, phone, country_code, city, postal_code, address_line1, "
                + "shipping_method, status, currency, subtotal, discount_total, shipping_total, tax_total, total, payment_expires_at",
            "ORDER-1", "guest@example.com", "Guest", "+361234567", "HU", "Budapest", "1000", "Address",
            "COURIER", "AWAITING_PAYMENT", "EUR", BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO,
            BigDecimal.ZERO, BigDecimal.TEN, Timestamp.from(Instant.now().plusSeconds(900)));
        UUID itemId = UUID.randomUUID();
        baseRow("order_items", itemId,
            "order_id, variant_id, sku_snapshot, product_name_snapshot, color_snapshot, size_snapshot, "
                + "unit_price, quantity, discount_total, tax_total, total",
            orderId, catalog.variantId(), "SKU-1", "Purchased name", "Black", "M", BigDecimal.TEN, 1,
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.TEN);
        jdbc.update("DELETE FROM product_variants WHERE id = ?", catalog.variantId());
        OrderItemEntity item = entityManager.find(OrderItemEntity.class, itemId);
        assertNull(item.getVariant());
        assertEquals("Purchased name", item.getProductNameSnapshot());
        assertEquals(new BigDecimal("10.0000"), item.getUnitPrice());
    }

    private UUID user() {
        UUID id = UUID.randomUUID();
        baseRow("users", id, "email, password_hash", id + "@example.com", "test-hash");
        return id;
    }

    private Catalog catalog() {
        UUID category = UUID.randomUUID();
        UUID product = UUID.randomUUID();
        UUID material = UUID.randomUUID();
        UUID color = UUID.randomUUID();
        UUID size = UUID.randomUUID();
        UUID variant = UUID.randomUUID();
        baseRow("categories", category, "name, slug", "Category", "category");
        baseRow("products", product, "category_id, name, slug, gender, status",
            category, "Product", "product", "UNISEX", "DRAFT");
        baseRow("materials", material, "name, code", "Cotton", "COTTON");
        baseRow("colors", color, "name, code", "Black", "BLACK");
        baseRow("sizes", size, "code, name, sort_order", "M", "Medium", 1);
        baseRow("product_variants", variant, "product_id, color_id, size_id, sku", product, color, size, "SKU-1");
        return new Catalog(category, product, material, variant);
    }

    private void baseRow(String table, UUID id, String columns, Object... values) {
        ArrayList<Object> parameters = new ArrayList<>(Arrays.asList(
            id, Timestamp.from(Instant.now()), Timestamp.from(Instant.now()), false));
        parameters.addAll(Arrays.asList(values));
        String placeholders = String.join(", ", Collections.nCopies(parameters.size(), "?"));
        jdbc.update("INSERT INTO " + table + " (id, created_at, updated_at, deleted, " + columns
            + ") VALUES (" + placeholders + ")", parameters.toArray());
    }

    private record Catalog(UUID categoryId, UUID productId, UUID materialId, UUID variantId) {
    }
}
