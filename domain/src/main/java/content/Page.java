package content;

import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Page extends BaseEntity {
    private String slug;
    private String title;
    private String content;
    private boolean isPublished;

    public Page(String slug, String title, String content) {
        this.slug = slug;
        this.title = title;
        this.content = content;
    }
}

