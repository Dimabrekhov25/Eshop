package org.infrastructure.persistence.content;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import org.infrastructure.persistence.common.BaseJpaEntity;

@Getter
@Entity
@Table(name = "pages", uniqueConstraints = {
    @UniqueConstraint(name = "uq_pages_slug", columnNames = {"slug"})
})
public class PageEntity extends BaseJpaEntity {

    @Column(name = "slug", nullable = false, length = 200)
    private String slug;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "is_published", nullable = false)
    private boolean isPublished;

    protected PageEntity() {
    }
}

