package org.devcalm.store.manager.domain.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "categories")
public class Category {
    @Id
    private ObjectId id;
    private String name;
    private String description;
    private ObjectId parentId;
    @Transient
    private Category parent;
    private boolean archived = false;
    @LastModifiedDate
    private Instant updatedAt;
    @CreatedDate
    private Instant createdAt;

    public static final String COLLECTION_NAME = "categories";
}
