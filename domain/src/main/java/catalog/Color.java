package catalog;

import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Color extends BaseEntity {
    private String name;
    private String code;
    private String hexCode; // Optional.

    public Color(String name, String code) {
        this.name = name;
        this.code = code;
    }
}

