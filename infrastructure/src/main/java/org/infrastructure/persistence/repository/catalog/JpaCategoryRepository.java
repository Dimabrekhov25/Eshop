package org.infrastructure.persistence.repository.catalog;

import catalog.Category;
import org.application.catalog.repository.CategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaCategoryRepository implements CategoryRepository {
    private final SpringDataCategoryRepository repository;
    public JpaCategoryRepository (SpringDataCategoryRepository repository){
        this.repository = repository;
    }
    @Override
    public Category save(Category category) {
        return repository.save(category);
    }
    @Override
    public Optional<Category> findById(UUID id) {
        return repository.findByIdAndDeletedFalse(id);
    }
    @Override
    public List<Category> findAll(){
        return repository.findAllByDeletedFalse();
    }
}
