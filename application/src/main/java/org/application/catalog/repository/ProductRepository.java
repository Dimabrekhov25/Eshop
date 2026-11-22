package org.application.catalog.repository;

import org.application.catalog.dto.PageQuery;
import org.application.catalog.dto.PageResult;
import org.application.catalog.dto.ProductListItem;

public interface ProductRepository {
    public PageResult<ProductListItem> findPublishedByCategory(String slug, PageQuery query);
}
