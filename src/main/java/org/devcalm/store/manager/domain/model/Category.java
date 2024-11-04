package org.devcalm.store.manager.domain.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "categories")
public class Category extends BaseEntity {
    private String name;
    private String description;
    private ObjectId parentId;
    @Transient
    private Category parent;

    public static final String COLLECTION_NAME = "categories";
}
