package catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ProductMaterialId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "material_id", nullable = false)
    private UUID materialId;
}

