package org.devcalm.store.manager.service.store;

import org.devcalm.store.manager.domain.model.Store;
import org.devcalm.store.manager.web.dto.SaveStoreRequest;
import org.devcalm.store.manager.web.dto.StoreDto;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper {

    public Store toEntity(SaveStoreRequest request) {
        return Store.builder()
                .name(request.name())
                .description(request.description())
                .addressInfo(request.addressInfo())
                .contactInfo(request.contactInfo())
                .categoryIds(request.categories())
                .tags(request.tags())
                .build();
    }

    public StoreDto toDto(Store entity) {
        return StoreDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .addressInfo(entity.getAddressInfo())
                .contactInfo(entity.getContactInfo())
                .categories(entity.getCategoryIds())
                .tags(entity.getTags())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
