package org.devcalm.store.manager.service.vendor;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.model.Category;
import org.devcalm.store.manager.domain.model.Product;
import org.devcalm.store.manager.domain.model.Store;
import org.devcalm.store.manager.domain.model.Vendor;
import org.devcalm.store.manager.domain.repository.VendorRepository;
import org.devcalm.store.manager.infrastructure.reflection.FieldNamesExtractor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class VendorFetcher {

    private final VendorRepository vendorRepository;
    private final ReactiveMongoTemplate template;
    private final FieldNamesExtractor fieldNamesExtractor;

    public Mono<Vendor> findById(ObjectId id) {
        return vendorRepository.findById(id)
                .switchIfEmpty(Mono.error(entityNotFound(id)));
    }

    public Mono<Vendor> findByIdWithRelations(ObjectId vendorId) {
        var match = Aggregation.match(where("_id").is(vendorId));

        var lookupCategories = Aggregation.lookup(Category.COLLECTION_NAME, "id", "vendorId", "categories_vendors");
        var lookupProducts = Aggregation.lookup(Product.COLLECTION_NAME, "id", "vendorId", "products_vendors");
        var lookupStores = Aggregation.lookup(Store.COLLECTION_NAME, "id", "vendorId", "stores_vendors");

        var project = Aggregation.project()
                .andInclude(fieldNamesExtractor.extract(Vendor.class).toArray(String[]::new))
                .and("categories_vendors._id").as("categoryIds")
                .and("products_vendors._id").as("productIds")
                .and("stores_vendors._id").as("storeIds");

        var aggregation = Aggregation.newAggregation(match, lookupCategories, lookupProducts, lookupStores, project);
        return template.aggregate(aggregation, Vendor.COLLECTION_NAME, Vendor.class)
                .next()
                .switchIfEmpty(Mono.error(entityNotFound(vendorId)));
    }

    private EntityNotFoundException entityNotFound(ObjectId vendorId) {
        return new EntityNotFoundException("Vendor %s is not found.".formatted(vendorId));
    }
}
