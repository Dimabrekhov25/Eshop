package org.application.catalog.command;

import catalog.Category;

import java.util.UUID;

public record CreateCategoryCommand(
        String name,
        String slug,
        UUID parentId
) {
}
