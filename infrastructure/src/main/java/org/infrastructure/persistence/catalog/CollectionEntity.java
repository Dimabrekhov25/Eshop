package org.infrastructure.persistence.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.infrastructure.persistence.common.BaseJpaEntity;

@Getter
@Entity
@Table(name = "collections", uniqueConstraints = {
    @UniqueConstraint(name = "uq_collections_slug", columnNames = {"slug"})
})
public class CollectionEntity extends BaseJpaEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "slug", nullable = false, length = 200)
    private String slug;

    @Column(name = "story", columnDefinition = "text")
    private String story;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @Column(name = "is_archived", nullable = false)
    private boolean isArchived;

    @OneToMany(mappedBy = "collection", fetch = FetchType.LAZY)
    private List<ProductEntity> products = new ArrayList<>();

    protected CollectionEntity() {
    }
}

