package org.application.catalog.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductListItem(
        UUID id,
        String name,
        String slug,
        String mainImageUrl,
        BigDecimal priceFrom,
        String currency
) {
}
