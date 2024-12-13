package org.devcalm.store.manager.service.vendor;

import org.devcalm.store.manager.domain.model.Vendor;
import org.devcalm.store.manager.web.dto.SaveVendorRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

class VendorMapperTest {

    private final VendorMapper mapper = new VendorMapper();

    @Test
    void toEntity() {
        var request = Instancio.of(SaveVendorRequest.class).create();
        var entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(request.name());
        assertThat(entity.getDescription()).isEqualTo(request.description());
    }

    @Test
    void toDto() {
        var entity = Instancio.of(Vendor.class).create();
        var dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getName()).isEqualTo(entity.getName());
        assertThat(dto.getDescription()).isEqualTo(entity.getDescription());
        assertThat(dto.getProducts()).isEqualTo(entity.getProductIds());
        assertThat(dto.getCategories()).isEqualTo(entity.getCategoryIds());
        assertThat(dto.getStores()).isEqualTo(entity.getStoreIds());
        assertThat(dto.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(entity.getUpdatedAt());
    }

    @Test
    void toDtoWithEmptyRelations() {
        var entity = Instancio.of(Vendor.class)
                .ignore(field(Vendor::getStoreIds))
                .ignore(field(Vendor::getProductIds))
                .ignore(field(Vendor::getCategoryIds))
                .create();
        var dto = mapper.toDto(entity);

        assertThat(dto.getProducts()).isEmpty();
        assertThat(dto.getCategories()).isEmpty();
        assertThat(dto.getStores()).isEmpty();
    }
}