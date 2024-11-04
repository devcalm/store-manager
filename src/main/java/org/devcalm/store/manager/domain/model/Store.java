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
@Document(collection = "stores")
public class Store extends BaseEntity {
    private String name;
    private String description;
    private AddressInfo addressInfo;
    private ContactInfo contactInfo;
    private List<ObjectId> categoryIds;
    private List<String> tags;

    public static final String COLLECTION_NAME = "stores";
}

