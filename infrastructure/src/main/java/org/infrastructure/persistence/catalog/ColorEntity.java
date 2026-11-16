package org.infrastructure.persistence.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import org.infrastructure.persistence.common.BaseJpaEntity;

@Getter
@Entity
@Table(name = "colors", uniqueConstraints = {
    @UniqueConstraint(name = "uq_colors_code", columnNames = {"code"})
})
public class ColorEntity extends BaseJpaEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @Column(name = "hex_code", length = 9)
    private String hexCode;

    protected ColorEntity() {
    }
}

