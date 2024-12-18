package org.devcalm.store.manager.service.category;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.exception.StoreException;
import org.devcalm.store.manager.domain.model.Category;
import org.devcalm.store.manager.domain.repository.CategoryRepository;
import org.devcalm.store.manager.domain.repository.projection.IdProjection;
import org.devcalm.store.manager.domain.repository.projection.IdsProjection;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GraphLookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryFetcher {

    private final ReactiveMongoTemplate template;
    private final CategoryRepository categoryRepository;

    public Mono<Category> findById(ObjectId id) {
        return categoryRepository.findByIdAndArchivedFalse(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Category %s is not found.".formatted(id))));
    }

    public Flux<ObjectId> fetchDescendants(ObjectId startWithId) {
        var graphLookup = GraphLookupOperation.builder()
                .from(Category.COLLECTION_NAME)
                .startWith("$_id")
                .connectFrom("_id")
                .connectTo("parentId")
                .as("descendants");

        var match = Aggregation.match(Criteria.where("_id").is(startWithId));
        var project = Aggregation.project().and("descendants._id").as("ids");

        var aggregation = Aggregation.newAggregation(match, graphLookup, project);
        return template.aggregate(aggregation, Category.COLLECTION_NAME, IdsProjection.class)
                .flatMap(c -> Flux.fromIterable(c.ids()));
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
                        return Mono.error(new StoreException("No categories were found."));
                    }
                    if (ids.size() != categories.size()) {
                        return Mono.error(new StoreException("Category IDs must have same length."));
                    }
                    return Mono.empty();
                });
    }
}
