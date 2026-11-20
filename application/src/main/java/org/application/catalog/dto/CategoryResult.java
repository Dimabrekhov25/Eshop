package org.application.catalog.dto;

import catalog.Category;

import java.util.UUID;

public record CategoryResult(
        UUID id,
        String name,
        String slug,
        UUID parentId
) {}
