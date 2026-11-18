package content;

import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "journal_articles", uniqueConstraints = {
    @UniqueConstraint(name = "uq_journal_articles_slug", columnNames = {"slug"})
})
public class JournalArticle extends BaseEntity {

    @Column(name = "slug", nullable = false, length = 200)
    @Setter
    private String slug;

    @Column(name = "title", nullable = false, length = 300)
    @Setter
    private String title;

    @Column(name = "summary", columnDefinition = "text")
    @Setter
    private String summary;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    @Setter
    private String content;

    @Column(name = "image_url", length = 2048)
    @Setter
    private String imageUrl;

    @Column(name = "published_at")
    @Setter
    private Instant publishedAt;

    protected JournalArticle() {
    }

    public JournalArticle(String slug, String title, String content) {
        this.slug = slug;
        this.title = title;
        this.content = content;
    }
}
