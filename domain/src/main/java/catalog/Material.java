package catalog;

import common.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Material extends BaseEntity {
    private String name;
    private String code;

    public Material(String name, String code) {
        this.name = name;
        this.code = code;
    }
}

