package org.infrastructure.persistence.repository.catalog.product;

import catalog.Product;
import catalog.enums.Status;
import org.application.catalog.dto.product.ProductListItem;
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
select new org.application.catalog.dto.product.ProductListItem(
    p.id,
    p.name,
    p.slug,
    (select i.url
     from ProductImage i
     where i.id = (select i2.id
                   from ProductImage i2
                   where i2.product = p and i2.deleted = false
                   order by i2.sortOrder asc, i2.id asc
                   limit 1)),
    (select min(pr.price)
     from ProductVariantPrice pr
     where pr.variant.product = p
       and pr.variant.isActive = true
       and pr.variant.deleted = false),
    (select min(pr2.currency)
     from ProductVariantPrice pr2
     where pr2.variant.product = p
       and pr2.variant.isActive = true
       and pr2.variant.deleted = false)
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
