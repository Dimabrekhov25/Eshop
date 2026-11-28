package org.application.catalog.command.category;

import java.util.UUID;

public record UpdateCategoryCommand(
        UUID id,
        String name,
        String slug,
        UUID parentId

) {
}
