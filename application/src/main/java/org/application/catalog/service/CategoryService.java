package org.application.catalog.service;

import catalog.Category;
import org.application.catalog.command.category.CreateCategoryCommand;
import org.application.catalog.command.category.UpdateCategoryCommand;
import org.application.catalog.dto.category.CategoryResult;
import org.application.catalog.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CategoryService {
    private CategoryRepository categoryRepository;
    public CategoryService (CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }
    @Transactional
    public CategoryResult createCategory (CreateCategoryCommand command){

    String name = command.name();
    String slug = command.slug();
    UUID parentId = command.parentId();
    Category parent = parentId == null ? null : optionalToCategory(parentId);

    Category saved = categoryRepository.save(new Category(name, slug, parent));

    return mapperToResult(saved);
    }

    public List<CategoryResult> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::mapperToResult)
                .toList();
    }

    @Transactional
    public void delete(UUID id) {
        optionalToCategory(id).softDelete();
    }
    @Transactional
    public CategoryResult update (UpdateCategoryCommand updateCategoryCommand){
        Category category = optionalToCategory(updateCategoryCommand.id());
        String name = updateCategoryCommand.name();
        String slug = updateCategoryCommand.slug();
        UUID parentId = updateCategoryCommand.parentId();
        Category parent = parentId == null ? null : optionalToCategory(parentId);


        category.setName(name);
        category.setSlug(slug);
        category.setParent(parent);
        return mapperToResult(category);
    }

    public CategoryResult findById (UUID id){
        return mapperToResult(optionalToCategory(id));
    }
    public Category optionalToCategory(UUID id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->new RuntimeException("error"));
        return category;
    }
    private UUID checkUuid (Category category){
        Category parent = category.getParent();
        UUID parentId =  parent == null ? null : parent.getId();
        return parentId;
    }
    private CategoryResult mapperToResult (Category category){

        return new CategoryResult(category.getId(), category.getName(),
                category.getSlug(), checkUuid(category));
    }

}
