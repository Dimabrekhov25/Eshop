-- Initial schema for the domain models. UUIDs and audit timestamps are supplied by the application.
-- No DROP, baseline-on-migrate or automatic Hibernate schema updates are used.

CREATE TABLE users (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    email VARCHAR(320) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE user_profiles (
    user_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    phone VARCHAR(40),
    CONSTRAINT pk_user_profiles PRIMARY KEY (user_id)
);

CREATE TABLE admin_profiles (
    user_id UUID NOT NULL,
    display_name VARCHAR(200) NOT NULL,
    role VARCHAR(32) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_admin_profiles PRIMARY KEY (user_id),
    CONSTRAINT ck_admin_profiles_role CHECK (role IN ('ADMIN', 'CONTENT_MANAGER', 'SUPPORT', 'WAREHOUSE'))
);

CREATE TABLE categories (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(200) NOT NULL,
    parent_id UUID,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uq_categories_slug UNIQUE (slug),
    CONSTRAINT ck_categories_parent_not_self CHECK (parent_id IS NULL OR parent_id <> id)
);

CREATE TABLE collections (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(200) NOT NULL,
    story TEXT,
    image_url VARCHAR(2048),
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_collections PRIMARY KEY (id),
    CONSTRAINT uq_collections_slug UNIQUE (slug)
);

CREATE TABLE colors (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(64) NOT NULL,
    hex_code VARCHAR(9),
    CONSTRAINT pk_colors PRIMARY KEY (id),
    CONSTRAINT uq_colors_code UNIQUE (code)
);

CREATE TABLE sizes (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(200) NOT NULL,
    sort_order INTEGER NOT NULL,
    CONSTRAINT pk_sizes PRIMARY KEY (id),
    CONSTRAINT uq_sizes_code UNIQUE (code)
);

CREATE TABLE materials (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(64) NOT NULL,
    CONSTRAINT pk_materials PRIMARY KEY (id),
    CONSTRAINT uq_materials_code UNIQUE (code)
);

CREATE TABLE products (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    category_id UUID NOT NULL,
    collection_id UUID,
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(200) NOT NULL,
    short_description TEXT,
    description TEXT,
    origin_country VARCHAR(100),
    gender VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    fit VARCHAR(200),
    fabric_weight_gsm NUMERIC(10,2),
    model_height_cm NUMERIC(10,2),
    model_size_id UUID,
    size_guide TEXT,
    care_instructions TEXT,
    is_essential BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_products PRIMARY KEY (id),
    CONSTRAINT uq_products_slug UNIQUE (slug),
    CONSTRAINT ck_products_gender CHECK (gender IN ('MEN', 'WOMEN', 'UNISEX')),
    CONSTRAINT ck_products_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'HIDDEN')),
    CONSTRAINT ck_products_fabric_weight_positive CHECK (fabric_weight_gsm IS NULL OR fabric_weight_gsm > 0),
    CONSTRAINT ck_products_model_height_positive CHECK (model_height_cm IS NULL OR model_height_cm > 0)
);

CREATE TABLE product_materials (
    product_id UUID NOT NULL,
    material_id UUID NOT NULL,
    percentage NUMERIC(5,2) NOT NULL,
    CONSTRAINT pk_product_materials PRIMARY KEY (product_id, material_id),
    CONSTRAINT ck_product_materials_percentage CHECK (percentage > 0 AND percentage <= 100)
);

CREATE TABLE product_variants (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    product_id UUID NOT NULL,
    color_id UUID NOT NULL,
    size_id UUID NOT NULL,
    sku VARCHAR(128) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_product_variants PRIMARY KEY (id),
    CONSTRAINT uq_product_variants_sku UNIQUE (sku),
    CONSTRAINT uq_product_variants_product_id_color_id_size_id UNIQUE (product_id, color_id, size_id)
);

CREATE TABLE product_variant_prices (
    variant_id UUID NOT NULL,
    currency VARCHAR(3) NOT NULL,
    price NUMERIC(19,4) NOT NULL,
    old_price NUMERIC(19,4),
    CONSTRAINT pk_product_variant_prices PRIMARY KEY (variant_id),
    CONSTRAINT ck_product_variant_prices_price_nonnegative CHECK (price >= 0),
    CONSTRAINT ck_product_variant_prices_old_price_nonnegative CHECK (old_price IS NULL OR old_price >= 0)
);

CREATE TABLE inventory (
    variant_id UUID NOT NULL,
    stock_quantity INTEGER NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_inventory PRIMARY KEY (variant_id),
    CONSTRAINT ck_inventory_stock_nonnegative CHECK (stock_quantity >= 0)
);

CREATE TABLE product_images (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    product_id UUID NOT NULL,
    color_id UUID,
    url VARCHAR(2048) NOT NULL,
    alt_text TEXT,
    sort_order INTEGER NOT NULL,
    CONSTRAINT pk_product_images PRIMARY KEY (id)
);

CREATE TABLE promo_codes (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    code VARCHAR(64) NOT NULL,
    discount_type VARCHAR(32) NOT NULL,
    discount_value NUMERIC(19,4) NOT NULL,
    min_order_amount NUMERIC(19,4),
    usage_limit INTEGER,
    starts_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP(6) WITH TIME ZONE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_promo_codes PRIMARY KEY (id),
    CONSTRAINT uq_promo_codes_code UNIQUE (code),
    CONSTRAINT ck_promo_codes_discount_type CHECK (discount_type IN ('PERCENT', 'FIXED')),
    CONSTRAINT ck_promo_codes_discount_value CHECK (discount_value > 0 AND (discount_type <> 'PERCENT' OR discount_value <= 100)),
    CONSTRAINT ck_promo_codes_minimum_nonnegative CHECK (min_order_amount IS NULL OR min_order_amount >= 0),
    CONSTRAINT ck_promo_codes_usage_limit_positive CHECK (usage_limit IS NULL OR usage_limit > 0),
    CONSTRAINT ck_promo_codes_date_range CHECK (expires_at IS NULL OR expires_at > starts_at)
);

CREATE TABLE carts (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    user_id UUID,
    guest_id UUID,
    promo_code_id UUID,
    status VARCHAR(32) NOT NULL,
    CONSTRAINT pk_carts PRIMARY KEY (id),
    CONSTRAINT ck_carts_status CHECK (status IN ('ACTIVE', 'CHECKED_OUT', 'MERGED')),
    CONSTRAINT ck_carts_one_owner CHECK ((user_id IS NOT NULL AND guest_id IS NULL) OR (user_id IS NULL AND guest_id IS NOT NULL))
);

CREATE TABLE cart_items (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    cart_id UUID NOT NULL,
    variant_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT pk_cart_items PRIMARY KEY (id),
    CONSTRAINT uq_cart_items_cart_id_variant_id UNIQUE (cart_id, variant_id),
    CONSTRAINT ck_cart_items_quantity_positive CHECK (quantity > 0)
);

CREATE TABLE orders (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    number VARCHAR(64) NOT NULL,
    user_id UUID,
    promo_code_id UUID,
    email VARCHAR(320) NOT NULL,
    recipient_name VARCHAR(200) NOT NULL,
    phone VARCHAR(40) NOT NULL,
    country_code VARCHAR(2) NOT NULL,
    city VARCHAR(200) NOT NULL,
    postal_code VARCHAR(32) NOT NULL,
    address_line1 VARCHAR(500) NOT NULL,
    address_line2 VARCHAR(500),
    shipping_method VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    subtotal NUMERIC(19,4) NOT NULL,
    discount_total NUMERIC(19,4) NOT NULL,
    shipping_total NUMERIC(19,4) NOT NULL,
    tax_total NUMERIC(19,4) NOT NULL,
    total NUMERIC(19,4) NOT NULL,
    promo_code_snapshot VARCHAR(64),
    carrier VARCHAR(100),
    tracking_number VARCHAR(200),
    payment_expires_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    shipped_at TIMESTAMP(6) WITH TIME ZONE,
    delivered_at TIMESTAMP(6) WITH TIME ZONE,
    cancelled_at TIMESTAMP(6) WITH TIME ZONE,
    expired_at TIMESTAMP(6) WITH TIME ZONE,
    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT uq_orders_number UNIQUE (number),
    CONSTRAINT ck_orders_shipping_method CHECK (shipping_method IN ('COURIER', 'PICKUP')),
    CONSTRAINT ck_orders_status CHECK (status IN ('AWAITING_PAYMENT', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'EXPIRED')),
    CONSTRAINT ck_orders_amounts_nonnegative CHECK (subtotal >= 0 AND discount_total >= 0 AND shipping_total >= 0 AND tax_total >= 0 AND total >= 0)
);

CREATE TABLE order_items (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    order_id UUID NOT NULL,
    variant_id UUID,
    sku_snapshot VARCHAR(128) NOT NULL,
    product_name_snapshot VARCHAR(200) NOT NULL,
    color_snapshot VARCHAR(200) NOT NULL,
    size_snapshot VARCHAR(200) NOT NULL,
    unit_price NUMERIC(19,4) NOT NULL,
    quantity INTEGER NOT NULL,
    discount_total NUMERIC(19,4) NOT NULL,
    tax_total NUMERIC(19,4) NOT NULL,
    total NUMERIC(19,4) NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id),
    CONSTRAINT ck_order_items_quantity_positive CHECK (quantity > 0),
    CONSTRAINT ck_order_items_amounts_nonnegative CHECK (unit_price >= 0 AND discount_total >= 0 AND tax_total >= 0 AND total >= 0)
);

CREATE TABLE payments (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    order_id UUID NOT NULL,
    provider VARCHAR(100) NOT NULL,
    provider_payment_id VARCHAR(255),
    amount NUMERIC(19,4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(32) NOT NULL,
    paid_at TIMESTAMP(6) WITH TIME ZONE,
    CONSTRAINT pk_payments PRIMARY KEY (id),
    CONSTRAINT uq_payments_provider_provider_payment_id UNIQUE (provider, provider_payment_id),
    CONSTRAINT ck_payments_status CHECK (status IN ('PENDING', 'PAID', 'FAILED', 'CANCELLED')),
    CONSTRAINT ck_payments_amount_nonnegative CHECK (amount >= 0)
);

CREATE TABLE wishlist_items (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    user_id UUID NOT NULL,
    product_id UUID NOT NULL,
    CONSTRAINT pk_wishlist_items PRIMARY KEY (id),
    CONSTRAINT uq_wishlist_items_user_id_product_id UNIQUE (user_id, product_id)
);

CREATE TABLE reviews (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    user_id UUID NOT NULL,
    product_id UUID NOT NULL,
    order_item_id UUID NOT NULL,
    rating INTEGER NOT NULL,
    text TEXT NOT NULL,
    fit_rating VARCHAR(32),
    status VARCHAR(32) NOT NULL,
    CONSTRAINT pk_reviews PRIMARY KEY (id),
    CONSTRAINT uq_reviews_order_item_id UNIQUE (order_item_id),
    CONSTRAINT ck_reviews_fit_rating CHECK (fit_rating IN ('SMALL', 'TRUE_TO_SIZE', 'LARGE')),
    CONSTRAINT ck_reviews_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    CONSTRAINT ck_reviews_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE TABLE stock_alerts (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    variant_id UUID NOT NULL,
    email VARCHAR(320) NOT NULL,
    notified_at TIMESTAMP(6) WITH TIME ZONE,
    CONSTRAINT pk_stock_alerts PRIMARY KEY (id)
);

CREATE TABLE pages (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    slug VARCHAR(200) NOT NULL,
    title VARCHAR(300) NOT NULL,
    content TEXT NOT NULL,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_pages PRIMARY KEY (id),
    CONSTRAINT uq_pages_slug UNIQUE (slug)
);

CREATE TABLE journal_articles (
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    slug VARCHAR(200) NOT NULL,
    title VARCHAR(300) NOT NULL,
    summary TEXT,
    content TEXT NOT NULL,
    image_url VARCHAR(2048),
    published_at TIMESTAMP(6) WITH TIME ZONE,
    CONSTRAINT pk_journal_articles PRIMARY KEY (id),
    CONSTRAINT uq_journal_articles_slug UNIQUE (slug)
);

-- Foreign keys are explicit because the persistence models keep domain references as UUIDs.
ALTER TABLE user_profiles ADD CONSTRAINT fk_user_profiles_user_id
    FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE admin_profiles ADD CONSTRAINT fk_admin_profiles_user_id
    FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE categories ADD CONSTRAINT fk_categories_parent_id
    FOREIGN KEY (parent_id) REFERENCES categories (id);
ALTER TABLE products ADD CONSTRAINT fk_products_category_id
    FOREIGN KEY (category_id) REFERENCES categories (id);
ALTER TABLE products ADD CONSTRAINT fk_products_collection_id
    FOREIGN KEY (collection_id) REFERENCES collections (id);
ALTER TABLE products ADD CONSTRAINT fk_products_model_size_id
    FOREIGN KEY (model_size_id) REFERENCES sizes (id);
ALTER TABLE product_materials ADD CONSTRAINT fk_product_materials_product_id
    FOREIGN KEY (product_id) REFERENCES products (id);
ALTER TABLE product_materials ADD CONSTRAINT fk_product_materials_material_id
    FOREIGN KEY (material_id) REFERENCES materials (id);
ALTER TABLE product_variants ADD CONSTRAINT fk_product_variants_product_id
    FOREIGN KEY (product_id) REFERENCES products (id);
ALTER TABLE product_variants ADD CONSTRAINT fk_product_variants_color_id
    FOREIGN KEY (color_id) REFERENCES colors (id);
ALTER TABLE product_variants ADD CONSTRAINT fk_product_variants_size_id
    FOREIGN KEY (size_id) REFERENCES sizes (id);
ALTER TABLE product_variant_prices ADD CONSTRAINT fk_product_variant_prices_variant_id
    FOREIGN KEY (variant_id) REFERENCES product_variants (id);
ALTER TABLE inventory ADD CONSTRAINT fk_inventory_variant_id
    FOREIGN KEY (variant_id) REFERENCES product_variants (id);
ALTER TABLE product_images ADD CONSTRAINT fk_product_images_product_id
    FOREIGN KEY (product_id) REFERENCES products (id);
ALTER TABLE product_images ADD CONSTRAINT fk_product_images_color_id
    FOREIGN KEY (color_id) REFERENCES colors (id);
ALTER TABLE carts ADD CONSTRAINT fk_carts_user_id
    FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE carts ADD CONSTRAINT fk_carts_promo_code_id
    FOREIGN KEY (promo_code_id) REFERENCES promo_codes (id);
ALTER TABLE cart_items ADD CONSTRAINT fk_cart_items_cart_id
    FOREIGN KEY (cart_id) REFERENCES carts (id);
ALTER TABLE cart_items ADD CONSTRAINT fk_cart_items_variant_id
    FOREIGN KEY (variant_id) REFERENCES product_variants (id);
ALTER TABLE orders ADD CONSTRAINT fk_orders_user_id
    FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE orders ADD CONSTRAINT fk_orders_promo_code_id
    FOREIGN KEY (promo_code_id) REFERENCES promo_codes (id);
ALTER TABLE order_items ADD CONSTRAINT fk_order_items_order_id
    FOREIGN KEY (order_id) REFERENCES orders (id);
ALTER TABLE order_items ADD CONSTRAINT fk_order_items_variant_id
    FOREIGN KEY (variant_id) REFERENCES product_variants (id) ON DELETE SET NULL;
ALTER TABLE payments ADD CONSTRAINT fk_payments_order_id
    FOREIGN KEY (order_id) REFERENCES orders (id);
ALTER TABLE wishlist_items ADD CONSTRAINT fk_wishlist_items_user_id
    FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE wishlist_items ADD CONSTRAINT fk_wishlist_items_product_id
    FOREIGN KEY (product_id) REFERENCES products (id);
ALTER TABLE reviews ADD CONSTRAINT fk_reviews_user_id
    FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE reviews ADD CONSTRAINT fk_reviews_product_id
    FOREIGN KEY (product_id) REFERENCES products (id);
ALTER TABLE reviews ADD CONSTRAINT fk_reviews_order_item_id
    FOREIGN KEY (order_item_id) REFERENCES order_items (id);
ALTER TABLE stock_alerts ADD CONSTRAINT fk_stock_alerts_variant_id
    FOREIGN KEY (variant_id) REFERENCES product_variants (id);

-- PostgreSQL does not automatically create indexes for referencing foreign-key columns.
CREATE INDEX ix_categories_parent_id ON categories (parent_id);
CREATE INDEX ix_products_category_id ON products (category_id);
CREATE INDEX ix_products_collection_id ON products (collection_id);
CREATE INDEX ix_products_model_size_id ON products (model_size_id);
CREATE INDEX ix_product_materials_material_id ON product_materials (material_id);
CREATE INDEX ix_product_variants_color_id ON product_variants (color_id);
CREATE INDEX ix_product_variants_size_id ON product_variants (size_id);
CREATE INDEX ix_product_images_product_id_sort_order ON product_images (product_id, sort_order);
CREATE INDEX ix_product_images_color_id ON product_images (color_id);
CREATE INDEX ix_carts_user_id ON carts (user_id);
CREATE INDEX ix_carts_promo_code_id ON carts (promo_code_id);
CREATE INDEX ix_cart_items_variant_id ON cart_items (variant_id);
CREATE INDEX ix_orders_user_id ON orders (user_id);
CREATE INDEX ix_orders_promo_code_id ON orders (promo_code_id);
CREATE INDEX ix_order_items_order_id ON order_items (order_id);
CREATE INDEX ix_order_items_variant_id ON order_items (variant_id);
CREATE INDEX ix_payments_order_id ON payments (order_id);
CREATE INDEX ix_wishlist_items_product_id ON wishlist_items (product_id);
CREATE INDEX ix_reviews_user_id ON reviews (user_id);
CREATE INDEX ix_reviews_product_id ON reviews (product_id);
CREATE INDEX ix_stock_alerts_variant_id ON stock_alerts (variant_id);

-- Only one outstanding alert per variant and normalized email; notified/deleted alerts remain historical.
CREATE UNIQUE INDEX uq_stock_alerts_pending_email
    ON stock_alerts (variant_id, LOWER(email))
    WHERE notified_at IS NULL AND deleted = FALSE;

