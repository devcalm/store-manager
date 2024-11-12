package org.devcalm.store.manager.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.List;

public record SaveProductRequest(
        @NotBlank
        String name,
        @NotNull
        BigDecimal price,
        double discount,
        int timeTakenInMinutes,
        @NotBlank
        String description,
        String notes,
        List<ObjectId> categories
) {
}
