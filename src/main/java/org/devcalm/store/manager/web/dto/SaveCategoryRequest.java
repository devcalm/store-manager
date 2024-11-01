package org.devcalm.store.manager.web.dto;

import jakarta.validation.constraints.NotBlank;
import org.bson.types.ObjectId;

public record SaveCategoryRequest(
        @NotBlank
        String name,
        @NotBlank
        String description,
        ObjectId parentId
) {
    public boolean hasParent() {
        return parentId != null;
    }
}
