package org.devcalm.store.manager.domain.repository;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.model.Category;
import org.devcalm.store.manager.domain.repository.projection.IdProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface CategoryRepository extends ReactiveMongoRepository<Category, ObjectId> {

    Mono<Category> findByIdAndArchivedFalse(ObjectId id);

    Flux<Category> findAllByArchivedFalse();

    Mono<Long> countByArchivedFalse();

    Flux<Category> findAllByArchivedFalse(Pageable pageable);

    Flux<IdProjection> findByIdInAndArchivedIsFalse(Collection<ObjectId> ids);
}
