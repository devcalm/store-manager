package org.devcalm.store.manager.domain.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "vendors")
public class Vendor extends BaseEntity {
    private String name;
    private String description;
    private List<ObjectId> storeIds;
    private List<ObjectId> categoryIds;
    private List<ObjectId> productIds;

    public static final String COLLECTION_NAME = "vendors";
}
