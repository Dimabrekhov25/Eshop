package content;

import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "pages", uniqueConstraints = {
    @UniqueConstraint(name = "uq_pages_slug", columnNames = {"slug"})
})
public class Page extends BaseEntity {

    @Column(name = "slug", nullable = false, length = 200)
    @Setter
    private String slug;

    @Column(name = "title", nullable = false, length = 300)
    @Setter
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    @Setter
    private String content;

    @Column(name = "is_published", nullable = false)
    @Setter
    private boolean isPublished;

    protected Page() {
    }

    public Page(String slug, String title, String content) {
        this.slug = slug;
        this.title = title;
        this.content = content;
    }
}
