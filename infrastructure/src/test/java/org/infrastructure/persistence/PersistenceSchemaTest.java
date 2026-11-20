package org.infrastructure.persistence;

import accounts.AdminProfile;
import accounts.enums.StaffRole;
import cart.Cart;
import cart.CartItem;
import cart.PromoCode;
import cart.enums.DiscountType;
import catalog.Collection;
import catalog.Color;
import catalog.Inventory;
import catalog.ProductImage;
import catalog.ProductVariant;
import catalog.Size;
import catalog.enums.Gender;
import catalog.enums.Status;
import content.JournalArticle;
import content.Page;
import discovery.Review;
import discovery.StockAlert;
import discovery.WishlistItem;
import orders.Order;
import orders.Payment;
import orders.enums.ShippingMethod;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import accounts.UserProfile;
import accounts.User;
import catalog.Category;
import catalog.Product;
import catalog.Material;
import catalog.ProductMaterial;
import catalog.ProductMaterialId;
import catalog.ProductVariantPrice;
import orders.OrderItem;
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
import java.time.Duration;
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
        Product product = entityManager.find(Product.class, catalog.productId());
        assertEquals(catalog.productId(), product.getId());
        assertNotNull(product.getCreatedAt());
        assertEquals(Gender.UNISEX, product.getGender());
        assertEquals(Status.DRAFT, product.getStatus());
        assertEquals(catalog.categoryId(), product.getCategory().getId());
        assertEquals("category", product.getCategory().getSlug());
        assertEquals("category", entityManager.find(Category.class, catalog.categoryId()).getSlug());
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

        assertEquals("Customer", entityManager.find(UserProfile.class, userId).getName());
        assertEquals(userId, entityManager.find(UserProfile.class, userId).getUser().getId());
        ProductMaterial composition = entityManager.find(ProductMaterial.class,
            new ProductMaterialId(catalog.productId(), catalog.materialId()));
        assertEquals(new BigDecimal("95.50"), composition.getPercentage());
        assertEquals(catalog.productId(), composition.getId().getProductId());
        assertEquals(catalog.productId(), composition.getProduct().getId());
        assertEquals(catalog.materialId(), composition.getMaterial().getId());
        assertEquals(new BigDecimal("12.3456"), entityManager
            .find(ProductVariantPrice.class, catalog.variantId()).getPrice());
        assertEquals(catalog.variantId(), entityManager
            .find(ProductVariantPrice.class, catalog.variantId()).getVariant().getId());
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

        Category category = entityManager.find(Category.class, catalog.categoryId());
        assertEquals(childId, category.getChildren().get(0).getId());
        assertEquals(catalog.categoryId(), category.getChildren().get(0).getParent().getId());
        Product product = category.getProducts().get(0);
        assertEquals(catalog.productId(), product.getId());
        assertEquals(catalog.variantId(), product.getVariants().get(0).getId());
        assertEquals("https://example.com/first.jpg", product.getImages().get(0).getUrl());
        assertEquals("Cotton", product.getMaterials().get(0).getMaterial().getName());
        assertEquals(catalog.productId(), entityManager.find(Material.class, catalog.materialId())
            .getProductMaterials().get(0).getProduct().getId());
    }

    @Test
    void persistsCompositeLinkUsingMappedAssociations() {
        Catalog catalog = catalog();
        ProductMaterial link = new ProductMaterial(
            entityManager.getReference(Product.class, catalog.productId()),
            entityManager.getReference(Material.class, catalog.materialId()),
            new BigDecimal("100.00"));
        entityManager.persist(link);
        entityManager.flush();
        entityManager.clear();

        ProductMaterial loaded = entityManager.find(ProductMaterial.class,
            new ProductMaterialId(catalog.productId(), catalog.materialId()));
        assertEquals(new BigDecimal("100.00"), loaded.getPercentage());
        assertEquals("Product", loaded.getProduct().getName());
        assertEquals("Cotton", loaded.getMaterial().getName());
    }

    @Test
    void derivesSharedPrimaryKeyFromOneToOneAssociation() {
        UUID userId = user();
        UserProfile profile = new UserProfile(
            entityManager.getReference(User.class, userId), "Customer", null);
        entityManager.persist(profile);
        entityManager.flush();
        assertEquals(userId, profile.getUserId());
        entityManager.clear();
        assertEquals(userId, entityManager.find(UserProfile.class, userId).getUser().getId());
    }

    @Test
    void persistsCatalogDomainModelsAndUpdatesTheSameIdentity() {
        Category category = new Category("Clothing", "clothing");
        Category child = new Category("T-Shirts", "t-shirts", category);
        Collection collection = new Collection("Essentials", "essentials");
        Color color = new Color("Black", "BLACK");
        Size size = new Size("M", "Medium", 1);
        Material material = new Material("Cotton", "COTTON");
        Product product = new Product(child, "T-Shirt", "t-shirt", Gender.UNISEX);
        product.setCollection(collection);
        product.setModelSize(size);
        ProductVariant variant = new ProductVariant(product, color, size, "TEE-BLACK-M");
        Inventory inventory = new Inventory(variant, 5);
        ProductVariantPrice price = new ProductVariantPrice(variant, "EUR", new BigDecimal("25.50"));
        ProductMaterial composition = new ProductMaterial(product, material, new BigDecimal("100"));
        ProductImage image = new ProductImage("https://example.com/front.jpg", "Front", 0, product);
        image.setColor(color);
        product.addImage(image);

        for (Object model : new Object[] {category, child, collection, color, size, material,
                product, variant, inventory, price, composition}) {
            entityManager.persist(model);
        }
        UUID productId = product.getId();
        Instant createdAt = product.getCreatedAt();
        entityManager.flush();
        entityManager.clear();

        Product loaded = entityManager.find(Product.class, productId);
        assertEquals(productId, loaded.getId());
        assertTrue(Duration.between(createdAt, loaded.getCreatedAt()).abs().toNanos() < 1000);
        assertEquals(category.getId(), loaded.getCategory().getParent().getId());
        assertEquals(collection.getId(), loaded.getCollection().getId());
        assertEquals(size.getId(), loaded.getModelSize().getId());
        assertEquals(image.getId(), loaded.getImages().get(0).getId());
        assertEquals(variant.getId(), loaded.getVariants().get(0).getId());
        assertEquals(material.getId(), loaded.getMaterials().get(0).getMaterial().getId());
        assertEquals(variant.getId(), entityManager.find(Inventory.class, variant.getId()).getVariantId());
        assertEquals(new BigDecimal("25.5000"),
            entityManager.find(ProductVariantPrice.class, variant.getId()).getPrice());

        Instant previousUpdate = loaded.getUpdatedAt();
        loaded.setName("Updated T-Shirt");
        entityManager.flush();
        entityManager.clear();

        Product updated = entityManager.find(Product.class, productId);
        assertEquals("Updated T-Shirt", updated.getName());
        assertTrue(updated.getUpdatedAt().isAfter(previousUpdate));
        assertEquals(1, jdbc.queryForObject("SELECT count(*) FROM products", Integer.class));
    }

    @Test
    void persistsAccountsCartOrdersDiscoveryAndContentUsingTheSameModels() {
        Catalog catalog = catalog();
        Product product = entityManager.getReference(Product.class, catalog.productId());
        ProductVariant variant = entityManager.getReference(ProductVariant.class, catalog.variantId());
        User user = new User("customer@example.com", "test-hash");
        UserProfile profile = new UserProfile(user, "Customer", "+361234567");
        AdminProfile admin = new AdminProfile(user, "Administrator", StaffRole.values()[0]);
        PromoCode promo = new PromoCode("WELCOME", DiscountType.values()[0], BigDecimal.TEN, Instant.now());
        Cart cart = Cart.forUser(user);
        cart.setPromoCode(promo);
        CartItem cartItem = new CartItem(cart, variant, 2);
        Order order = new Order("ORDER-DOMAIN", user.getEmail(), "Customer", "+361234567", "HU",
            "Budapest", "1000", "Address", ShippingMethod.COURIER, "EUR", BigDecimal.TEN,
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.TEN, Instant.now().plusSeconds(900));
        order.setUser(user);
        order.setPromoCode(promo);
        OrderItem item = new OrderItem(order, variant, "SKU-1", "Purchased product", "Black", "M",
            BigDecimal.TEN, 1, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.TEN);
        Payment payment = new Payment(order, "test-provider", BigDecimal.TEN, "EUR");
        WishlistItem wish = new WishlistItem(user, product);
        Review review = new Review(user, product, item, 5, "Good product");
        StockAlert alert = new StockAlert(variant, user.getEmail());
        Page page = new Page("delivery", "Delivery", "Delivery information");
        JournalArticle article = new JournalArticle("news", "News", "Article content");

        for (Object model : new Object[] {user, profile, admin, promo, cart, cartItem, order,
                item, payment, wish, review, alert, page, article}) {
            entityManager.persist(model);
        }
        entityManager.flush();
        entityManager.clear();

        assertEquals(user.getId(), entityManager.find(UserProfile.class, user.getId()).getUser().getId());
        assertTrue(entityManager.find(AdminProfile.class, user.getId()).isActive());
        Cart loadedCart = entityManager.find(Cart.class, cart.getId());
        assertEquals(user.getId(), loadedCart.getUser().getId());
        assertEquals(promo.getId(), loadedCart.getPromoCode().getId());
        assertEquals(2, loadedCart.getItems().get(0).getQuantity());
        Order loadedOrder = entityManager.find(Order.class, order.getId());
        assertEquals(item.getId(), loadedOrder.getItems().get(0).getId());
        assertEquals(payment.getId(), loadedOrder.getPayments().get(0).getId());
        assertEquals("Purchased product", loadedOrder.getItems().get(0).getProductNameSnapshot());
        assertEquals(product.getId(), entityManager.find(WishlistItem.class, wish.getId()).getProduct().getId());
        assertEquals(item.getId(), entityManager.find(Review.class, review.getId()).getOrderItem().getId());
        assertEquals(variant.getId(), entityManager.find(StockAlert.class, alert.getId()).getVariant().getId());
        assertEquals("Delivery information", entityManager.find(Page.class, page.getId()).getContent());
        assertEquals("Article content", entityManager.find(JournalArticle.class, article.getId()).getContent());
    }

    @Test
    void removingGalleryImagePersistsRemovalWithoutDeletingTheProduct() {
        Catalog catalog = catalog();
        Product product = entityManager.find(Product.class, catalog.productId());
        ProductImage image = new ProductImage("https://example.com/front.jpg", "Front", 0, product);
        product.addImage(image);
        entityManager.flush();
        entityManager.clear();

        Product loaded = entityManager.find(Product.class, catalog.productId());
        loaded.removeImage(image.getId());
        entityManager.flush();
        entityManager.clear();

        assertNull(entityManager.find(ProductImage.class, image.getId()));
        assertNotNull(entityManager.find(Product.class, catalog.productId()));
        assertTrue(entityManager.find(Product.class, catalog.productId()).getImages().isEmpty());
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
        OrderItem item = entityManager.find(OrderItem.class, itemId);
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
