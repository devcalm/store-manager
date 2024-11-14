package org.devcalm.store.manager.service.vendor;

import org.devcalm.store.manager.domain.model.Vendor;
import org.devcalm.store.manager.web.dto.SaveVendorRequest;
import org.devcalm.store.manager.web.dto.VendorDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class VendorMapper {

    public Vendor toEntity(SaveVendorRequest request) {
        return Vendor.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }

    public VendorDto toDto(Vendor entity) {
        return VendorDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .categories(Objects.requireNonNullElse(entity.getCategoryIds(), List.of()))
                .products(Objects.requireNonNullElse(entity.getProductIds(), List.of()))
                .stores(Objects.requireNonNullElse(entity.getStoreIds(), List.of()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
