package org.devcalm.store.manager.domain.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "products")
public class Product extends BaseEntity {
    private String name;
    private BigDecimal price;
    private double discount;
    private int timeTakenInMinutes;
    private String description;
    private String notes;
    private List<ObjectId> categoryIds;
}
