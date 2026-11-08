package common;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class BaseEntity {
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private boolean deleted;

    protected BaseEntity() {
        Instant now = Instant.now();
        this.id = UUID.randomUUID();
        this.createdAt = now;
        this.updatedAt = now;
        this.deleted = false;
    }

    public void markUpdated() {
        this.updatedAt = Instant.now();
    }

    public void softDelete() {
        this.deleted = true;
        markUpdated();
    }
}
