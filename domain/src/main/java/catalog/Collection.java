package catalog;

import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "collections", uniqueConstraints = {
    @UniqueConstraint(name = "uq_collections_slug", columnNames = {"slug"})
})
public class Collection extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    @Setter
    private String name;

    @Column(name = "slug", nullable = false, length = 200)
    @Setter
    private String slug;

    @Column(name = "story", columnDefinition = "text")
    @Setter
    private String story;

    @Column(name = "image_url", length = 2048)
    @Setter
    private String imageUrl;

    @Column(name = "is_archived", nullable = false)
    @Setter
    private boolean isArchived;

    @OneToMany(mappedBy = "collection", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    protected Collection() {
    }

    public Collection(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public List<Product> getProducts() {
        return List.copyOf(products);
    }
}
