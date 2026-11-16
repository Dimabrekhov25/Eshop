package org.infrastructure.persistence.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.infrastructure.persistence.common.BaseJpaEntity;

@Getter
@Entity
@Table(name = "materials", uniqueConstraints = {
    @UniqueConstraint(name = "uq_materials_code", columnNames = {"code"})
})
public class MaterialEntity extends BaseJpaEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
    private List<ProductMaterialEntity> productMaterials = new ArrayList<>();

    protected MaterialEntity() {
    }
}

