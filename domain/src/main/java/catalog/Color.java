package catalog;

import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "colors", uniqueConstraints = {
    @UniqueConstraint(name = "uq_colors_code", columnNames = {"code"})
})
public class Color extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    @Setter
    private String name;

    @Column(name = "code", nullable = false, length = 64)
    @Setter
    private String code;

    @Column(name = "hex_code", length = 9)
    @Setter
    private String hexCode;

    protected Color() {
    }

    public Color(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
