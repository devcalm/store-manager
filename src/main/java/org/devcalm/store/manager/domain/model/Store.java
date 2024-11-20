package org.devcalm.store.manager.domain.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = Store.COLLECTION_NAME)
public class Store extends BaseEntity {
    private String name;
    private String description;
    private AddressInfo addressInfo;
    private ContactInfo contactInfo;
    @Builder.Default
    private List<ObjectId> categoryIds = new ArrayList<>();
    private List<String> tags;
    private ObjectId vendorId;
    @Builder.Default
    private List<ProductCustomization> productCustomizations = new ArrayList<>();

    public static final String COLLECTION_NAME = "stores";
}

