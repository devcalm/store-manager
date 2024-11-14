package org.devcalm.store.manager.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateVendorRequest(
        @NotBlank
        String name,
        @NotBlank
        String description
) {
}
