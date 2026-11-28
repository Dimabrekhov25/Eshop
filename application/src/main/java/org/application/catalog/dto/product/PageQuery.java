package org.application.catalog.dto.product;

public record PageQuery<T>(
     int page,
     int size
) {}