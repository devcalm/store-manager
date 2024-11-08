package org.devcalm.store.manager.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public abstract class BaseEntity {
    @Id
    protected ObjectId id;

    @Builder.Default
    protected boolean archived = false;

    @LastModifiedDate
    protected Instant updatedAt;

    @CreatedDate
    protected Instant createdAt;

    public Instant getUpdatedAt() {
        return updatedAt != null ? updatedAt.truncatedTo(ChronoUnit.MILLIS) : null;
    }

    public Instant getCreatedAt() {
        return createdAt != null ? createdAt.truncatedTo(ChronoUnit.MILLIS) : null;
    }
}
