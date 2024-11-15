package org.devcalm.store.manager.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.model.AddressInfo;
import org.devcalm.store.manager.domain.model.ContactInfo;
import org.devcalm.store.manager.domain.model.ProductCustomization;

import java.util.List;

public record SaveStoreRequest(
        @NotBlank
        String name,
        @NotBlank
        String description,
        @NotNull
        AddressInfo addressInfo,
        @NotNull
        ContactInfo contactInfo,
        List<ObjectId> categories,
        List<String> tags,
        List<ProductCustomization> products
) {
}
