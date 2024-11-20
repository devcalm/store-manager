package org.devcalm.store.manager.domain.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = Product.COLLECTION_NAME)
public class Product extends BaseEntity {
    private String name;
    private BigDecimal price;
    private double discount;
    private int timeTakenInMinutes;
    private String description;
    private String notes;
    @Builder.Default
    private List<ObjectId> categoryIds = new ArrayList<>();
    private ObjectId vendorId;

    public static final String COLLECTION_NAME = "products";
}
