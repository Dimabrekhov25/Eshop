package org.application.catalog.command;

import catalog.Category;

import java.util.UUID;

public record UpdateCategoryCommand(
        UUID id,
        String name,
        String slug,
        UUID parentId

) {
}
