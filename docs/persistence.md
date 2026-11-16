# Persistence and database initialization

The domain module remains independent of JPA. Its 25 persistent models have separate
`*Entity` classes under `org.infrastructure.persistence`. `Product.images` is stored
in `product_images`, not in a second column or a serialized list.

## How the schema is created

1. Start the existing PostgreSQL Compose service: `docker compose up -d postgres_db`.
2. Supply `DB_ADMIN_PASSWORD` to the Java launch configuration as before.
3. Start `api.MyEshopApplication` or run `:api:bootRun`.

Spring Boot discovers `PersistenceConfiguration` through the API component scan.
Its `@EntityScan` discovers all persistence packages. Flyway applies
`infrastructure/src/main/resources/db/migration/V1__init_shop.sql` before Hibernate
validates the mapped tables using the existing `ddl-auto=validate` setting.
The existing single PostgreSQL account and `public` schema are used.

Flyway does not create the PostgreSQL server or the database itself: Compose already
creates `eshop_local`. The migration creates its tables, constraints and indexes.
If a nonempty schema has already been created manually, inspect it before applying
V1. Do not enable `baseline-on-migrate`, delete the volume or edit existing data
just to bypass a migration error. Already applied migrations must remain immutable;
subsequent changes go in V2, V3, etc.

## Mapping choices

- BaseJpaEntity supplies UUID, creation/update instants and the soft-delete flag.
  UUIDs and audit timestamps continue to be supplied by application/domain code.
- UserProfile/AdminProfile use user_id as their key; Inventory and
  ProductVariantPrice use variant_id. There is one current price per variant,
  matching the existing diagrams, not a key per currency.
- ProductMaterial uses the composite key (product_id, material_id), represented
  by an @EmbeddedId ProductMaterialId. Its two @ManyToOne associations use @MapsId
  to share the foreign-key columns with the composite key. The association entity
  remains necessary because the relationship stores percentage.
- Enum values are stored as VARCHAR(32), using EnumType.STRING. Reordering enum
  constants does not change existing values. Renaming a constant requires a data
  migration. Domain enum types are reused without adding JPA to domain.
- Money/discount amounts use NUMERIC(19,4), material shares NUMERIC(5,2), and
  measurements NUMERIC(10,2). PostgreSQL may round values exceeding the scale;
  application validation must reject unsupported precision before persistence.
- Instant fields use TIMESTAMP(6) WITH TIME ZONE. Long content uses TEXT rather
  than @Lob, and image/file values are URLs rather than database binary objects.
- Foreign references are object associations: @ManyToOne for ordinary references
  and @OneToOne for unique/shared-key references. @JoinColumn names the existing
  database column and foreign-key constraint. Profiles, inventory and current
  prices use @MapsId for their shared primary keys. Cart.guestId remains a UUID:
  it identifies an anonymous guest and is not a foreign key to a user.
- Useful inverse @OneToMany collections include category children/products,
  collection products, product variants/images/materials, material compositions,
  cart items and order items/payments. mappedBy refers to the Java association
  field on the owning side. The owning side must be set when creating/updating a
  link; adding an object only to an inverse collection does not update the FK.
- All associations request LAZY loading. Repositories/services must choose a
  fetch plan (queries, fetch joins or entity graphs) for the use case and map to
  domain/DTO objects within the transaction. Do not serialize JPA entities as REST
  responses or depend on open-in-view. No blanket cascade REMOVE or orphanRemoval
  is enabled, especially for references to shared catalog/history records.
- Slugs/codes and user email are unique across all rows, including soft-deleted
  ones. Account email normalization belongs to the application. Pending stock
  alerts have a case-insensitive partial unique index and can be subscribed to
  again after notification or soft deletion.
- OrderItem keeps purchase snapshots. Its optional variant reference becomes NULL
  on physical variant deletion; the purchased name, SKU and amounts survive.
- The migration checks quantities, stock, ratings, material shares, discount bounds,
  enum values and the exclusive user/guest ownership of a cart. Multi-row rules
  (e.g. total material share of 100%, review ownership, stock reservations and
  exactly-once callbacks) still require transactional application logic.

The change from scalar foreign-key fields to JPA associations preserves the
existing columns and constraints, so V1 is unchanged and no V2 is needed solely
for this Java mapping change. Future physical schema changes require a new version.

Persistence classes currently describe storage and support loading by Hibernate.
Domain rehydration, mappers, CRUD repositories and controllers are separate next
steps: the existing domain constructors generate new IDs and must not be used
unchanged to restore existing records.

## Exporting a draft DDL

For future mapping changes, use the optional `ddl-export` Spring profile. Supply
`DDL_EXPORT_PATH` with an absolute output file path whose parent directory exists,
for example `C:/Main/JavaShop/Eshop/api/build/schema-draft.sql`, together with the
usual database password, then run:

```powershell
.\gradlew.bat :api:bootRun --args="--spring.profiles.active=ddl-export"
```

The profile disables Flyway and database schema actions, and exports a draft from
JPA metadata, including the mapped foreign keys and association table. Review the
output: the partial stock-alert index, supporting indexes and some business checks
are deliberately defined only in the migration. A full DDL export is not
an incremental migration and must not be applied over existing tables.

See [JPA Buddy and Flyway workflow](jpa-buddy-workflow.md) for migration placement,
the optional IDE tooling and the complete model-to-migration process.

## Verification

The API context smoke test uses an isolated H2 database and Hibernate-generated
tables, with Flyway disabled. This checks application wiring, not PostgreSQL SQL.

The infrastructure integration test uses actual PostgreSQL: Flyway applies V1,
Hibernate validates all 25 entity mappings, and tests exercise constraints, shared
and composite keys, association navigation/persistence, enum/decimal loading and
historical order snapshots. To run
against a dedicated disposable database whose name ends in `_test`:

```powershell
$env:ESHOP_SCHEMA_TEST_URL = 'jdbc:postgresql://localhost:25432/eshop_schema_test'
$env:ESHOP_SCHEMA_TEST_USER = 'postgres'
# Supply ESHOP_SCHEMA_TEST_PASSWORD if that test database requires a password.
.\gradlew.bat :infrastructure:test :api:test :domain:test
```

Never point this test URL at the development or production database. Without
ESHOP_SCHEMA_TEST_URL, the PostgreSQL integration class is skipped. Individual
test data changes are rolled back after each test; the schema migration remains
in the dedicated test database.
