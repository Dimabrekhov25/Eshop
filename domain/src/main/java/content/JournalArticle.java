package content;

import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class JournalArticle extends BaseEntity {
    private String slug;
    private String title;
    private String summary; // Optional.
    private String content;
    private String imageUrl; // Optional.
    private Instant publishedAt; // Optional; null means draft.

    public JournalArticle(String slug, String title, String content) {
        this.slug = slug;
        this.title = title;
        this.content = content;
    }
}

