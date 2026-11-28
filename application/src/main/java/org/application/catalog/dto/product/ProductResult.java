package org.application.catalog.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResult(
        UUID id,
        UUID categoryId,
        String name,
        String slug,
        String shortDescription,
        String description,
        String originCountry,
        String gender,
        String status,
        String fit,
        BigDecimal fabricWeightGsm,
        BigDecimal modelHeightCm,
        String sizeGuide,
        String careInstructions,
        boolean essential
) {}
