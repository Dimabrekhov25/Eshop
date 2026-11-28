package org.application.catalog.command.category;

import java.util.UUID;

public record CreateCategoryCommand(
        String name,
        String slug,
        UUID parentId
) {
}
