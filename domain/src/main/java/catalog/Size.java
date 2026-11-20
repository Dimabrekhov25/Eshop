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
@Table(name = "sizes", uniqueConstraints = {
    @UniqueConstraint(name = "uq_sizes_code", columnNames = {"code"})
})
public class Size extends BaseEntity {

    @Column(name = "code", nullable = false, length = 64)
    @Setter
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    @Setter
    private String name;

    @Column(name = "sort_order", nullable = false)
    @Setter
    private int sortOrder;

    protected Size() {
    }

    public Size(String code, String name, int sortOrder) {
        this.code = code;
        this.name = name;
        this.sortOrder = sortOrder;
    }
}
