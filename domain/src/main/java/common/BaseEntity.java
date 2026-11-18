package common;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    protected BaseEntity() {
        Instant now = Instant.now();
        this.id = UUID.randomUUID();
        this.createdAt = now;
        this.updatedAt = now;
        this.deleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        markUpdated();
    }

    public void markUpdated() {
        this.updatedAt = Instant.now();
    }

    public void softDelete() {
        this.deleted = true;
        markUpdated();
    }
}
