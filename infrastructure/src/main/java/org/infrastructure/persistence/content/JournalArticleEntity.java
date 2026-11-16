package org.infrastructure.persistence.content;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import org.infrastructure.persistence.common.BaseJpaEntity;

@Getter
@Entity
@Table(name = "journal_articles", uniqueConstraints = {
    @UniqueConstraint(name = "uq_journal_articles_slug", columnNames = {"slug"})
})
public class JournalArticleEntity extends BaseJpaEntity {

    @Column(name = "slug", nullable = false, length = 200)
    private String slug;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "summary", columnDefinition = "text")
    private String summary;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @Column(name = "published_at")
    private Instant publishedAt;

    protected JournalArticleEntity() {
    }
}

