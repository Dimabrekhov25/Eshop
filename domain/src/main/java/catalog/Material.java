package catalog;

import common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "materials", uniqueConstraints = {
    @UniqueConstraint(name = "uq_materials_code", columnNames = {"code"})
})
public class Material extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    @Setter
    private String name;

    @Column(name = "code", nullable = false, length = 64)
    @Setter
    private String code;

    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
    private List<ProductMaterial> productMaterials = new ArrayList<>();

    protected Material() {
    }

    public Material(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public List<ProductMaterial> getProductMaterials() {
        return List.copyOf(productMaterials);
    }
}
