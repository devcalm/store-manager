package org.devcalm.store.manager.service.category;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.model.Category;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Update.update;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
@RequiredArgsConstructor
public class CategoryRepositorySupport {

    private final ReactiveMongoTemplate template;

    public Mono<Void> archiveCategories(Flux<ObjectId> idsToArchive) {
        return idsToArchive.collectList()
                .flatMap(ids -> template.updateMulti(
                        query(where("_id").in(ids).and("archived").is(false)),
                        update("archived", true),
                        Category.class))
                .then();
    }
}
