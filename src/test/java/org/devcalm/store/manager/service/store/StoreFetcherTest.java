package org.devcalm.store.manager.service.store;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.MongoTestConfig;
import org.devcalm.store.manager.data.ProductTestData;
import org.devcalm.store.manager.data.StoreTestData;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.exception.StoreException;
import org.devcalm.store.manager.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;
import java.util.List;
import java.util.stream.Collectors;

@DataMongoTest
@ContextConfiguration(classes = MongoTestConfig.class)
@ComponentScan(basePackageClasses = {StoreFetcher.class, StoreTestData.class, ProductTestData.class},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = StoreService.class)}
)
class StoreFetcherTest {

    @Autowired
    private StoreTestData storeTestData;
    @Autowired
    private ProductTestData productTestData;
    @Autowired
    private StoreFetcher fetcher;

    @Test
    void shouldFindById() {
        var store = storeTestData.createStore();

        StepVerifier.create(fetcher.findById(store.getId()))
                .expectNext(store)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionIfStoreNotFound() {
        var id = new ObjectId();
        StepVerifier.create(fetcher.findById(id))
                .expectErrorMatches(throwable -> throwable instanceof EntityNotFoundException &&
                        throwable.getMessage().equals("Store %s is not found.".formatted(id)))
                .verify();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldReturnEmptyIfNoProductIdsWerePassed(List<ObjectId> products) {
        StepVerifier.create(fetcher.checkExistProducts(products))
                .verifyComplete();
    }

    @Test
    void shouldCheckExistProducts() {
        var products = productTestData.createProducts(5);
        var productIds = products.stream().map(Product::getId).toList();

        StepVerifier.create(fetcher.checkExistProducts(productIds))
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionIfProductNotFound() {
        StepVerifier.create(fetcher.checkExistProducts(List.of(new ObjectId())))
                .expectErrorMatches(throwable -> throwable instanceof StoreException &&
                        throwable.getMessage().equals("No products were found."))
                .verify();
    }

    @Test
    void shouldThrowExceptionForDifferentCollectionSize() {
        var products = productTestData.createProducts(2);
        var productIds = products.stream().map(Product::getId).collect(Collectors.toList());
        productIds.add(new ObjectId());

        StepVerifier.create(fetcher.checkExistProducts(productIds))
                .expectErrorMatches(throwable -> throwable instanceof StoreException &&
                        throwable.getMessage().equals("Product IDs must have same length."))
                .verify();
    }
}