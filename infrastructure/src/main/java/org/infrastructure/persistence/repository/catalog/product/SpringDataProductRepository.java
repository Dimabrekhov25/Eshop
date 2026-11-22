package org.infrastructure.persistence.repository.catalog.product;

import catalog.Product;
import catalog.enums.Status;
import org.application.catalog.dto.PageQuery;
import org.application.catalog.dto.ProductListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByIdAndDeletedFalse(UUID id);

    @Query( value = """
select new org.application.catalog.dto.ProductListItem(
p.id, p.name, p.slug, p.shortDescription
)
from Product p
where p.deleted = false
and p.status = :status
and p.category.slug = :categorySlug
""",
            countQuery = """
select count(p.id)
from Product p 
where p.deleted = false
and p.status = :status
and p.category.slug = :categorySlug
"""
    )
    Page<ProductListItem> findPage(@Param("categorySlug") String categorySlug,
                                   @Param("status")Status status,
                                   Pageable pageable
    );

}
