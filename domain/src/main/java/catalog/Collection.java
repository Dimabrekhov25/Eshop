package catalog;

import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Collection extends BaseEntity {
    private String name;
    private String slug;
    private String story; // Optional.
    private String imageUrl; // Optional.
    private boolean isArchived;

    public Collection(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}

