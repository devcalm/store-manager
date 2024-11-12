package org.devcalm.store.manager.service.store;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.MongoTestConfig;
import org.devcalm.store.manager.data.StoreTestDataService;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.service.category.CategoryFetcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

@DataMongoTest
@ContextConfiguration(classes = MongoTestConfig.class)
@ComponentScan(basePackageClasses = {StoreFetcher.class, StoreTestDataService.class, CategoryFetcher.class})
class StoreFetcherTest {

    @Autowired
    private StoreTestDataService storeTestDataService;
    @Autowired
    private StoreFetcher storeFetcher;

    @Test
    void shouldFindById() {
        var store = storeTestDataService.createStore();

        StepVerifier.create(storeFetcher.findById(store.getId()))
                .expectNext(store)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionIfStoreNotFound() {
        var id = new ObjectId();
        StepVerifier.create(storeFetcher.findById(id))
                .expectErrorMatches(throwable -> throwable instanceof EntityNotFoundException &&
                        throwable.getMessage().equals("Store %s is not found.".formatted(id)))
                .verify();
    }
}