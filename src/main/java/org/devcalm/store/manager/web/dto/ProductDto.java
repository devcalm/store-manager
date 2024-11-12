package org.devcalm.store.manager.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.infrastructure.serializer.ObjectIdListSerializer;
import org.devcalm.store.manager.infrastructure.serializer.ObjectIdSerializer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;
    private String name;
    private BigDecimal price;
    private double discount;
    private String description;
    private String notes;
    @JsonSerialize(using = ObjectIdListSerializer.class)
    private List<ObjectId> categories;
    private Instant updatedAt;
    private Instant createdAt;
}
