package org.devcalm.store.manager.domain.repository;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.model.Store;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StoreRepository extends ReactiveMongoRepository<Store, ObjectId> {

    Mono<Store> findByIdAndArchivedFalse(ObjectId id);

    Flux<Store> findAllByArchivedFalse(Pageable pageable);

    Mono<Long> countByArchivedFalse();
}
