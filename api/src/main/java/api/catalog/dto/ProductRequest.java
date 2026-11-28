package api.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequest(
        @NotNull
        UUID categoryId,

        @NotBlank
        @Size(max = 200)
        String name,

        @NotBlank
        @Size(max = 200)
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "slug must be lowercase letters, digits and hyphens")
        String slug,

        String shortDescription,
        String description,

        @Size(max = 100)
        String originCountry,

        @NotBlank
        String gender,

        @Size(max = 200)
        String fit,

        @PositiveOrZero
        BigDecimal fabricWeightGsm,

        @PositiveOrZero
        BigDecimal modelHeightCm,

        String sizeGuide,
        String careInstructions,
        boolean essential
) {}
