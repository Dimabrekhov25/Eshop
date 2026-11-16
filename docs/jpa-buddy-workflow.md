# JPA associations, Flyway and JPA Buddy

## Project layout

```text
domain/src/main/java/...
    Business models and rules; no JPA dependencies.

infrastructure/src/main/java/org/infrastructure/persistence/
    PersistenceConfiguration.java
    accounts/*Entity.java
    catalog/*Entity.java
    cart/*Entity.java
    orders/*Entity.java
    discovery/*Entity.java
    content/*Entity.java

infrastructure/src/main/resources/db/migration/
    V1__init_shop.sql
    V2__description_of_next_schema_change.sql  (when needed)

api/src/main/resources/
    application.properties
    application-ddl-export.properties
```

For this single deployable application and database, keep migrations next to the
persistence implementation in infrastructure. The Flyway starter and PostgreSQL
support module already live in infrastructure/build.gradle.kts. The API depends
on infrastructure, so its resources are available at classpath:db/migration at
runtime. Do not duplicate migrations into api resources or Java source folders.

The API owns the startup/environment settings (connection, password source,
profiles, ddl-auto). A separate database-migrations module/job becomes useful if
several applications share the schema or deployment needs an independent migration
step. It is not required for the current project. Do not relocate an applied
migration just to restructure its version history.

## Runtime sequence

1. Docker runs PostgreSQL and initializes the database for a fresh volume.
2. Spring Boot reads the runtime configuration and prepares the DataSource.
3. Flyway checks flyway_schema_history and applies pending versioned SQL scripts.
4. Hibernate builds the persistence model and validates it against the migrated
   database with ddl-auto=validate. This is not a complete validator for every
   CHECK constraint, index or database-specific feature.
5. Application services call persistence adapters inside transactions; mappers
   translate between domain objects and JPA entities.

Flyway applies SQL. It does not infer Java changes. Hibernate maps entities and
associations. It can generate a DDL draft but does not replace a reviewed migration
history. JPA Buddy is an optional development-time generator; production does not
need IntelliJ or the plugin.

## Current relationship mapping

- ProductEntity.category is @ManyToOne; CategoryEntity.products is the inverse
  @OneToMany(mappedBy="category"). Both map products.category_id.
- CategoryEntity.parent/children map the self-referencing category hierarchy.
- UserProfileEntity.user and AdminProfileEntity.user are @OneToOne with @MapsId:
  user_id is both the profile primary key and a foreign key to users.id.
- InventoryEntity.variant and ProductVariantPriceEntity.variant use the same
  shared-primary-key pattern for variant_id.
- ProductMaterialEntity has @EmbeddedId ProductMaterialId and two @ManyToOne
  associations with @MapsId("productId") / @MapsId("materialId"). This represents
  many products using many materials, with percentage stored on each link.
- ReviewEntity.orderItem is @OneToOne because an order line can have at most one
  review. OrderItemEntity.variant remains optional; its @OnDelete(SET_NULL) matches
  the existing FK so purchased snapshot fields survive physical variant deletion.
- No associations cascade deletion of users, categories, materials or products.
  Deletion and soft-delete semantics must be implemented explicitly in use cases.

## Adding JPA Buddy later

Do not add a JPA Buddy Gradle dependency. It is an IntelliJ plugin. Install/enable a
version compatible with the IDE through Settings > Plugins > Marketplace when
ready. Feature availability depends on the IDE subscription: current JetBrains
documentation places migration generation among Ultimate features.

For IntelliJ 2026.2 and newer, database connections are configured through Database
Tools and SQL, not an independent JPA Buddy connection manager. Create an IDE
PostgreSQL datasource for localhost:15432, database eshop_local, using the existing
account. This IDE datasource is for development tools; Spring Boot continues to
use application.properties and its own password configuration.

JPA Buddy enables features per module based on dependencies. Work with the Entity
classes and migration directory in infrastructure, where JPA and Flyway already
exist. Choose that persistence model, not the plain domain classes, as the source.

## Normal change workflow

1. Bring the local comparison database to the current migration version. JPA
   validation alone does not initialize an empty schema; Flyway must have run.
2. Change the infrastructure entity mapping to describe the desired state.
   Align domain/use-case changes as appropriate.
3. In the IDE generate a Flyway versioned migration with Source = Model and
   Target = the local database or a model snapshot of the previous version.
4. Review the generated SQL. Preserve deliberate SQL-only constraints/indexes;
   resolve rename-vs-drop/add decisions and data backfills yourself. A generator
   cannot infer how existing data should be transformed.
5. Save the result in infrastructure/src/main/resources/db/migration as a NEW
   version, for example V2__add_product_attribute.sql. Do not regenerate V1 over
   an already applied V1. If only Java navigation changed and the physical schema
   is identical, no migration should be necessary.
6. Apply the migration through Flyway; let Hibernate validate the result. Test both
   a fresh database and upgrade from the preceding migration when changing schema.
7. Commit entity/domain changes, the migration and relevant tests together.

Avoid letting Hibernate update the comparison database before generating the
diff: the generator would then see the already changed schema instead of the
previous version. The ddl-export profile exports only a draft file and does not
apply DDL, so it remains a valid alternative when IDE generation is unavailable.

JPA Buddy's Init schema generation is intended for a new initial migration. This
project already has V1; use it and generate incremental changes afterwards.
Stock-alert partial uniqueness and database business checks remain authoritative
in SQL. Inspect generated DROP/ALTER proposals for objects not represented in JPA.

## Official references

- [JPA Buddy installation, module dependencies and IDE database tools](https://www.jetbrains.com/help/idea/jpa-buddy.html)
- [Model-to-database migration generation workflow](https://www.jetbrains.com/help/idea/database-versioning.html)
- [Feature availability](https://www.jetbrains.com/help/idea/jpa-buddy-paid-features.html)
- [Spring Boot and Flyway startup integration](https://docs.spring.io/spring-boot/how-to/data-initialization.html)
