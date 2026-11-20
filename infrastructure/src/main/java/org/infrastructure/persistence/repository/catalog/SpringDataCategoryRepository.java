package org.infrastructure.persistence.repository.catalog;

import catalog.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataCategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByIdAndDeletedFalse(UUID id);
    List<Category> findAllByDeletedFalse();
}
