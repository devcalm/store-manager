package org.devcalm.store.manager.service.vendor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.model.Category;
import org.devcalm.store.manager.domain.model.Product;
import org.devcalm.store.manager.domain.model.Store;
import org.devcalm.store.manager.domain.model.Vendor;
import org.devcalm.store.manager.domain.repository.VendorRepository;
import org.devcalm.store.manager.infrastructure.reflection.FieldNamesExtractor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class VendorFetcher {

    private final VendorRepository vendorRepository;
    private final ReactiveMongoTemplate template;
    private final FieldNamesExtractor fieldNamesExtractor;

    private static final String LOCAL_FIELD = "_id";
    private static final String FOREIGN_FIELD = "vendorId";

    public Mono<Vendor> findById(ObjectId id) {
        return vendorRepository.findByIdAndArchivedFalse(id)
                .switchIfEmpty(Mono.error(entityNotFound(id)));
    }

    public Mono<Vendor> findByIdWithRelations(ObjectId vendorId) {
        var match = Aggregation.match(where("_id").is(vendorId)
                .andOperator(where("archived").is(false)));
        var lookup = new AggregationLookup();

        var project = Aggregation.project()
                .andInclude(fieldNamesExtractor.extract(Vendor.class).toArray(String[]::new))
                .and("categories_vendors._id").as("categoryIds")
                .and("products_vendors._id").as("productIds")
                .and("stores_vendors._id").as("storeIds");

        var aggregation = Aggregation.newAggregation(match, lookup.getCategories(), lookup.getProducts(), lookup.getStores(), project);
        return template.aggregate(aggregation, Vendor.COLLECTION_NAME, Vendor.class)
                .next()
                .switchIfEmpty(Mono.error(entityNotFound(vendorId)));
    }

    public Flux<Vendor> findAllWithRelations(Pageable pageable) {
        var match = Aggregation.match(where("archived").is(false));
        var lookup = new AggregationLookup();

        var project = Aggregation.project()
                .andInclude(fieldNamesExtractor.extract(Vendor.class).toArray(String[]::new))
                .and("categories_vendors._id").as("categoryIds")
                .and("products_vendors._id").as("productIds")
                .and("stores_vendors._id").as("storeIds");

        var skip = Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize());
        var limit = Aggregation.limit(pageable.getPageSize());

        var aggregation = Aggregation.newAggregation(match, lookup.getCategories(), lookup.getProducts(), lookup.getStores(), project, skip, limit);
        return template.aggregate(aggregation, Vendor.COLLECTION_NAME, Vendor.class);
    }

    private EntityNotFoundException entityNotFound(ObjectId vendorId) {
        return new EntityNotFoundException("Vendor %s is not found.".formatted(vendorId));
    }

    @Getter
    private static class AggregationLookup {
        private final LookupOperation categories = Aggregation.lookup(Category.COLLECTION_NAME, LOCAL_FIELD, FOREIGN_FIELD, "categories_vendors");
        private final LookupOperation products = Aggregation.lookup(Product.COLLECTION_NAME, LOCAL_FIELD, FOREIGN_FIELD, "products_vendors");
        private final LookupOperation stores = Aggregation.lookup(Store.COLLECTION_NAME, LOCAL_FIELD, FOREIGN_FIELD, "stores_vendors");
    }
}
