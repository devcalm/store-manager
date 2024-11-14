package org.devcalm.store.manager.service.product;

import org.devcalm.store.manager.domain.model.Product;
import org.devcalm.store.manager.web.dto.SaveProductRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapper();

    @Test
    void toEntity() {
        var request = Instancio.of(SaveProductRequest.class).create();
        var entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(request.name());
        assertThat(entity.getPrice()).isEqualTo(request.price());
        assertThat(entity.getDiscount()).isEqualTo(request.discount());
        assertThat(entity.getTimeTakenInMinutes()).isEqualTo(request.timeTakenInMinutes());
        assertThat(entity.getDescription()).isEqualTo(request.description());
        assertThat(entity.getNotes()).isEqualTo(request.notes());
        assertThat(entity.getCategoryIds()).isEqualTo(request.categories());
    }

    @Test
    void toDto() {
        var entity = Instancio.of(Product.class).create();
        var dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo(entity.getName());
        assertThat(dto.getPrice()).isEqualTo(entity.getPrice());
        assertThat(dto.getDiscount()).isEqualTo(entity.getDiscount());
        assertThat(dto.getNotes()).isEqualTo(entity.getNotes());
        assertThat(dto.getDescription()).isEqualTo(entity.getDescription());
        assertThat(dto.getCategories()).isEqualTo(entity.getCategoryIds());
        assertThat(dto.getTimeTakenInMinutes()).isEqualTo(entity.getTimeTakenInMinutes());
        assertThat(dto.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(entity.getUpdatedAt());
    }
}