package org.application.catalog.dto;

import java.util.List;

public record PageQuery<T>(
     int page,
     int size
) {}