package org.devcalm.store.manager.service.store;

import org.devcalm.store.manager.domain.model.Store;
import org.devcalm.store.manager.web.dto.SaveStoreRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StoreMapperTest {

    private final StoreMapper mapper = new StoreMapper();

    @Test
    void toEntity() {
        var request = Instancio.of(SaveStoreRequest.class).create();
        var entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(request.name());
        assertThat(entity.getDescription()).isEqualTo(request.description());
        assertThat(entity.getAddressInfo()).isEqualTo(request.addressInfo());
        assertThat(entity.getContactInfo()).isEqualTo(request.contactInfo());
        assertThat(entity.getCategoryIds()).isEqualTo(request.categories());
        assertThat(entity.getTags()).isEqualTo(request.tags());
    }

    @Test
    void toDto() {
        var entity = Instancio.of(Store.class).create();
        var dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo(entity.getName());
        assertThat(dto.getDescription()).isEqualTo(entity.getDescription());
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getAddressInfo()).isEqualTo(entity.getAddressInfo());
        assertThat(dto.getContactInfo()).isEqualTo(entity.getContactInfo());
        assertThat(dto.getTags()).isEqualTo(entity.getTags());
        assertThat(dto.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(entity.getUpdatedAt());
    }
}