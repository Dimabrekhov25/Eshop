package api.catalog.dto;

import java.util.UUID;

public record CategoryRequest(
        String name,
        String slug,
        UUID parentId
) {}