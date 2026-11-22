package org.application.catalog.repository;

import catalog.Product;
import org.application.catalog.dto.PageQuery;
import org.application.catalog.dto.PageResult;
import org.application.catalog.dto.ProductListItem;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    public Product save(Product product);
    public Optional<Product> findById(UUID id);
    public PageResult<ProductListItem> findPublishedByCategory(String slug, PageQuery query);

}
