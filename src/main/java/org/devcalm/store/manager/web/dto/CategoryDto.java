package org.devcalm.store.manager.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.infrastructure.serializer.ObjectIdSerializer;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;
    private String name;
    private String description;
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId parentId;
    private Instant updatedAt;
    private Instant createdAt;
}
