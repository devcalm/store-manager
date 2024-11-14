package org.devcalm.store.manager.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = Category.COLLECTION_NAME)
public class Category extends BaseEntity {
    private String name;
    private String description;
    private ObjectId parentId;
    private ObjectId vendorId;

    public static final String COLLECTION_NAME = "categories";
}
