package org.devcalm.store.manager.service.store;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.MongoTestConfig;
import org.devcalm.store.manager.data.StoreTestData;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

@DataMongoTest
@ContextConfiguration(classes = MongoTestConfig.class)
@ComponentScan(basePackageClasses = {StoreFetcher.class, StoreTestData.class},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = StoreService.class)}
)
class StoreFetcherTest {

    @Autowired
    private StoreTestData storeTestData;
    @Autowired
    private StoreFetcher storeFetcher;

    @Test
    void shouldFindById() {
        var store = storeTestData.createStore();

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