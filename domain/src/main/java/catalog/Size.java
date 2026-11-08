package catalog;

import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Size extends BaseEntity {
    private String code;
    private String name;
    private int sortOrder;

    public Size(String code, String name, int sortOrder) {
        this.code = code;
        this.name = name;
        this.sortOrder = sortOrder;
    }
}

