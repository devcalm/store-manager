package org.devcalm.store.manager.service.product;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.model.Product;
import org.devcalm.store.manager.domain.repository.ProductRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductFetcher {

    private final ProductRepository productRepository;

    public Mono<Product> findById(ObjectId id) {
        return productRepository.findByIdAndArchivedFalse(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Product %s is not found.".formatted(id))));
    }
}
