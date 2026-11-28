package org.application.catalog.dto.category;

import java.util.UUID;

public record CategoryResult(
        UUID id,
        String name,
        String slug,
        UUID parentId
) {}
