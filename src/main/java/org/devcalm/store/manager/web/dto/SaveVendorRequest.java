package org.devcalm.store.manager.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SaveVendorRequest(
        @NotBlank
        String name,
        @NotBlank
        String description
) {
}
