package org.devcalm.store.manager.service.category;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.model.Category;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CategoryDBHandler {

    private final ReactiveMongoTemplate template;

    public Mono<Void> archiveCategories(Flux<ObjectId> idsToArchive) {
        return idsToArchive.collectList()
                .flatMap(ids -> {
                    var query = new Query();
                    query.addCriteria(Criteria.where("_id").in(ids).and("archived").is(false));

                    var update = new Update();
                    update.set("archived", true);

                    return template.updateMulti(query, update, Category.class);
                }).then();
    }

}
