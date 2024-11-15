package org.devcalm.store.manager.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.model.AddressInfo;
import org.devcalm.store.manager.domain.model.ContactInfo;
import org.devcalm.store.manager.domain.model.ProductCustomization;
import org.devcalm.store.manager.infrastructure.serializer.ObjectIdListSerializer;
import org.devcalm.store.manager.infrastructure.serializer.ObjectIdSerializer;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;
    private String name;
    private String description;
    private AddressInfo addressInfo;
    private ContactInfo contactInfo;
    @JsonSerialize(using = ObjectIdListSerializer.class)
    private List<ObjectId> categories;
    private List<String> tags;
    private List<ProductCustomization> products;
    private Instant updatedAt;
    private Instant createdAt;
}
