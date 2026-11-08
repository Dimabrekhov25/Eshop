package catalog;

import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Category extends BaseEntity {
    private String name;
    private String slug;
    private UUID parentId; // Optional; null for a root category.

    public Category(String name, String slug) {
        this(name, slug, null);
    }

    public Category(String name, String slug, UUID parentId) {
        this.name = name;
        this.slug = slug;
        this.parentId = parentId;
    }

    public void setParentId(UUID parentId) {
        if (getId().equals(parentId)) {
            throw new IllegalArgumentException("Category cannot be its own parent");
        }
        this.parentId = parentId;
        markUpdated();
    }
}

