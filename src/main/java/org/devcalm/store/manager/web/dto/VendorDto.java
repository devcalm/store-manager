package org.devcalm.store.manager.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.infrastructure.serializer.ObjectIdListSerializer;
import org.devcalm.store.manager.infrastructure.serializer.ObjectIdSerializer;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorDto {
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;
    @JsonSerialize(using = ObjectIdListSerializer.class)
    private List<ObjectId> categories;
    @JsonSerialize(using = ObjectIdListSerializer.class)
    private List<ObjectId> products;
    @JsonSerialize(using = ObjectIdListSerializer.class)
    private List<ObjectId> stores;
    private Instant updatedAt;
    private Instant createdAt;
}
