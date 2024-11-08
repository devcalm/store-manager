package org.devcalm.store.manager.service.store;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.MongoTestConfig;
import org.devcalm.store.manager.data.CategoryTestDataService;
import org.devcalm.store.manager.data.StoreTestDataService;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.exception.StoreException;
import org.devcalm.store.manager.domain.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DataMongoTest
@ContextConfiguration(classes = MongoTestConfig.class)
@ComponentScan(basePackageClasses = {StoreFetcher.class, StoreTestDataService.class, CategoryTestDataService.class})
class StoreFetcherTest {

    @Autowired
    private StoreTestDataService storeTestDataService;
    @Autowired
    private CategoryTestDataService categoryTestDataService;
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

    @ParameterizedTest
    @MethodSource("emptyList")
    void shouldReturnEmpty(List<ObjectId> categories) {
        StepVerifier.create(storeFetcher.checkExistCategories(categories))
                .verifyComplete();
    }

    private static Stream<Arguments> emptyList() {
        return Stream.of(
                Arguments.of(List.of()),
                Arguments.of((List<ObjectId>) null)
        );
    }

    @Test
    void shouldCheckExistCategories() {
        var categories = categoryTestDataService.createCategories(5);
        var categoryIds = categories.stream().map(Category::getId).toList();

        StepVerifier.create(storeFetcher.checkExistCategories(categoryIds))
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionIfCategoryNotFound() {
        StepVerifier.create(storeFetcher.checkExistCategories(List.of(new ObjectId())))
                .expectErrorMatches(throwable -> throwable instanceof StoreException &&
                        throwable.getMessage().equals("No categories were found."))
                .verify();
    }

    @Test
    void shouldThrowExceptionForDifferentCollectionSize() {
        var categories = categoryTestDataService.createCategories(2);
        var categoryIds = categories.stream().map(Category::getId).collect(Collectors.toList());
        categoryIds.add(new ObjectId());

        StepVerifier.create(storeFetcher.checkExistCategories(categoryIds))
                .expectErrorMatches(throwable -> throwable instanceof StoreException &&
                        throwable.getMessage().equals("Category IDs must have same length."))
                .verify();
    }
}