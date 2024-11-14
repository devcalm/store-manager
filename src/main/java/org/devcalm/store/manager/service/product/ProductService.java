package org.devcalm.store.manager.service.product;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.model.Vendor;
import org.devcalm.store.manager.domain.repository.ProductRepository;
import org.devcalm.store.manager.service.category.CategoryFetcher;
import org.devcalm.store.manager.web.dto.SaveProductRequest;
import org.devcalm.store.manager.web.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductFetcher productFetcher;
    private final CategoryFetcher categoryFetcher;
    private final ProductRepository productRepository;

    public Mono<ProductDto> create(Vendor vendor, SaveProductRequest request) {
        return categoryFetcher.checkExistCategories(request.categories())
                .then(Mono.defer(() -> {
                            var product = productMapper.toEntity(request);
                            product.setVendorId(vendor.getId());
                            return productRepository.save(product);
                        })
                        .map(productMapper::toDto));
    }

    public Mono<ProductDto> findById(ObjectId id) {
        return productFetcher.findById(id)
                .map(productMapper::toDto);
    }

    public Mono<Page<ProductDto>> findAll(Pageable pageable) {
        return productRepository.countByArchivedFalse()
                .zipWith(productRepository.findAllByArchivedFalse(pageable).map(productMapper::toDto).collectList())
                .map(tuple -> new PageImpl<>(tuple.getT2(), pageable, tuple.getT1()));
    }

    public Mono<Void> delete(ObjectId id) {
        return productFetcher.findById(id).flatMap(s -> {
            s.setArchived(true);
            return productRepository.save(s);
        }).then();
    }

    public Mono<ProductDto> update(ObjectId id, SaveProductRequest request) {
        return categoryFetcher.checkExistCategories(request.categories())
                .then(Mono.defer(() -> productFetcher.findById(id))
                        .flatMap(store -> {
                            store.setName(request.name());
                            store.setDescription(request.description());
                            store.setNotes(request.notes());
                            store.setDiscount(request.discount());
                            store.setPrice(request.price());
                            store.setTimeTakenInMinutes(request.timeTakenInMinutes());
                            store.setCategoryIds(request.categories());
                            return productRepository.save(store);
                        }).map(productMapper::toDto));
    }
}
