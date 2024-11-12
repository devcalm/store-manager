package org.devcalm.store.manager.service.store;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.model.Store;
import org.devcalm.store.manager.domain.repository.StoreRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class StoreFetcher {

    private final StoreRepository storeRepository;

    public Mono<Store> findById(ObjectId id) {
        return storeRepository.findByIdAndArchivedFalse(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Store %s is not found.".formatted(id))));
    }
}
