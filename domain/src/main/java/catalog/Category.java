package catalog;

import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_categories_slug",
                        columnNames = "slug"
                )
        }
)
public class Category extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    @Setter
    private String name;

    @Column(name = "slug", nullable = false, length = 200)
    @Setter
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_id", nullable = true,
        foreignKey = @ForeignKey(name = "fk_categories_parent_id"))
    private Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    protected Category() {
    }

    public Category(String name, String slug) {
        this(name, slug, null);
    }

    public Category(String name, String slug, Category parent) {
        this.name = name;
        this.slug = slug;
        setParent(parent);
    }

    public void setParent(Category parent) {
        if (parent != null && getId().equals(parent.getId())) {
            throw new IllegalArgumentException("Category cannot be its own parent");
        }
        this.parent = parent;
        markUpdated();
    }

    public List<Category> getChildren() {
        return List.copyOf(children);
    }

    public List<Product> getProducts() {
        return List.copyOf(products);
    }
}
