package org.devcalm.store.manager.service.store;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.exception.StoreException;
import org.devcalm.store.manager.domain.model.Store;
import org.devcalm.store.manager.domain.repository.CategoryRepository;
import org.devcalm.store.manager.domain.repository.StoreRepository;
import org.devcalm.store.manager.domain.repository.projection.IdProjection;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StoreFetcher {

    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;

    public Mono<Store> findById(ObjectId id) {
        return storeRepository.findByIdAndArchivedFalse(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Store %s is not found.".formatted(id))));
    }

    public Mono<Void> checkExistCategories(List<ObjectId> categories) {
        if (categories == null || categories.isEmpty()) {
            return Mono.empty();
        }
        return categoryRepository.findByIdInAndArchivedIsFalse(categories)
                .map(IdProjection::id)
                .collectList()
                .flatMap(ids -> {
                    if (ids.isEmpty()) {
                        return Mono.error(new StoreException("No categories were found"));
                    }
                    if (ids.size() != categories.size()) {
                        return Mono.error(new StoreException("Category IDs must have same length"));
                    }
                    return Mono.empty();
                });
    }
}
