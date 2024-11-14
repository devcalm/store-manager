package org.devcalm.store.manager.domain.model;

import lombok.Data;
import org.bson.types.ObjectId;

import java.math.BigDecimal;

@Data
public class ProductCustomization {
    private ObjectId productId;
    private BigDecimal price;
    private boolean isAvailable;
}
