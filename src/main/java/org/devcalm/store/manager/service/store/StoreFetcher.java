package org.devcalm.store.manager.service.store;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.exception.StoreException;
import org.devcalm.store.manager.domain.model.Store;
import org.devcalm.store.manager.domain.repository.ProductRepository;
import org.devcalm.store.manager.domain.repository.StoreRepository;
import org.devcalm.store.manager.domain.repository.projection.IdProjection;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StoreFetcher {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public Mono<Store> findById(ObjectId id) {
        return storeRepository.findByIdAndArchivedFalse(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Store %s is not found.".formatted(id))));
    }

    public Mono<Void> checkExistProducts(List<ObjectId> products) {
        if (products == null || products.isEmpty()) {
            return Mono.empty();
        }
        return productRepository.findByIdInAndArchivedIsFalse(products)
                .map(IdProjection::id)
                .collectList()
                .flatMap(ids -> {
                    if (ids.isEmpty()) {
                        return Mono.error(new StoreException("No products were found."));
                    }
                    if (ids.size() != products.size()) {
                        return Mono.error(new StoreException("Product IDs must have same length."));
                    }
                    return Mono.empty();
                });
    }
}
