package org.devcalm.store.manager.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCustomization {
    private ObjectId productId;
    private BigDecimal price;
    private boolean isAvailable;
}
