package org.devcalm.store.manager.service.product;

import org.devcalm.store.manager.domain.model.Product;
import org.devcalm.store.manager.web.dto.ProductDto;
import org.devcalm.store.manager.web.dto.SaveProductRequest;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(SaveProductRequest request) {
        return Product.builder()
                .name(request.name())
                .price(request.price())
                .discount(request.discount())
                .timeTakenInMinutes(request.timeTakenInMinutes())
                .description(request.description())
                .notes(request.notes())
                .categoryIds(request.categories())
                .build();
    }

    public ProductDto toDto(Product entity) {
        return ProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .notes(entity.getNotes())
                .price(entity.getPrice())
                .discount(entity.getDiscount())
                .timeTakenInMinutes(entity.getTimeTakenInMinutes())
                .categories(entity.getCategoryIds())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
