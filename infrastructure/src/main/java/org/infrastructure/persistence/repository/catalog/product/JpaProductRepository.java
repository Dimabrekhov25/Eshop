package org.infrastructure.persistence.repository.catalog.product;

import catalog.Product;
import catalog.enums.Status;
import org.application.catalog.dto.PageQuery;
import org.application.catalog.dto.PageResult;
import org.application.catalog.dto.ProductListItem;
import org.application.catalog.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProductRepository implements ProductRepository {

    private final SpringDataProductRepository repository;
    public JpaProductRepository (SpringDataProductRepository repository){
        this.repository = repository;
    }

    public Product save(Product product)
    {return repository.save(product);}

    public Optional<Product> findById(UUID id){
        return repository.findByIdAndDeletedFalse(id);
    }

    @Override
    public PageResult<ProductListItem> findPublishedByCategory(
            String categorySlug,
            PageQuery query){

        Pageable pageable = PageRequest.of(
                query.page(),
                query.size(),
                Sort.by(Sort.Direction.DESC, "createdAt")
                        .and(Sort.by("id")));

        Page<ProductListItem> page = repository.findPage(
                categorySlug,
                Status.PUBLISHED,
                pageable);

        return new PageResult<ProductListItem>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

}
