package org.devcalm.store.manager.domain.repository;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveMongoRepository<Product, ObjectId> {

    Mono<Product> findByIdAndArchivedFalse(ObjectId id);

    Flux<Product> findAllByArchivedFalse(Pageable pageable);

    Mono<Long> countByArchivedFalse();
}
