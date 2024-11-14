package org.devcalm.store.manager.domain.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = Vendor.COLLECTION_NAME)
public class Vendor extends BaseEntity {
    private String name;
    private String description;
    @ReadOnlyProperty
    private List<ObjectId> storeIds;
    @ReadOnlyProperty
    private List<ObjectId> categoryIds;
    @ReadOnlyProperty
    private List<ObjectId> productIds;

    public static final String COLLECTION_NAME = "vendors";
}
