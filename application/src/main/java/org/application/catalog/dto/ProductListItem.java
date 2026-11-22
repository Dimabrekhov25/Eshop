package org.application.catalog.dto;

import java.util.UUID;

public record ProductListItem(
        UUID id,
        String name,
        String slug,
        String shortDescription

) {
}
