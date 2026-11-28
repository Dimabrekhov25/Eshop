package org.application.catalog.service;

import org.application.catalog.dto.product.PageQuery;
import org.application.catalog.dto.product.PageResult;
import org.application.catalog.dto.product.ProductListItem;
import org.application.catalog.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private ProductRepository repository ;

    private ProductService (ProductRepository repository){
        this.repository = repository;
    }

    public PageResult<ProductListItem> findPage(String slug, PageQuery query){
        return repository.findPublishedByCategory(slug,query);
    }

}
