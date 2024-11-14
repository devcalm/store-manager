package org.devcalm.store.manager.service.category;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.MongoTestConfig;
import org.devcalm.store.manager.data.CategoryTestData;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.exception.StoreException;
import org.devcalm.store.manager.domain.model.Category;
import org.devcalm.store.manager.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ContextConfiguration(classes = MongoTestConfig.class)
@ComponentScan(basePackageClasses = {CategoryService.class, CategoryTestData.class})
class CategoryFetcherTest {

    @Autowired
    private CategoryFetcher fetcher;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryTestData categoryTestData;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll().block();
    }

    @Test
    void shouldFindById() {
        var category = categoryTestData.createCategory();

        StepVerifier.create(fetcher.findById(category.getId()))
                .expectNext(category)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        var id = new ObjectId();

        StepVerifier.create(fetcher.findById(id))
                .expectErrorMatches(throwable -> throwable instanceof EntityNotFoundException &&
                        throwable.getMessage().equals("Category %s is not found.".formatted(id)))
                .verify();
    }

    @Test
    void shouldFetchDescendants() {
        var child = categoryTestData.createCategoryWithParent();
        var parent = categoryRepository.findById(child.getParentId()).block();

        assertThat(parent).isNotNull();

        StepVerifier.create(fetcher.fetchDescendants(parent.getId()))
                .expectNext(child.getId())
                .verifyComplete();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldReturnEmpty(List<ObjectId> categories) {
        StepVerifier.create(fetcher.checkExistCategories(categories))
                .verifyComplete();
    }

    @Test
    void shouldCheckExistCategories() {
        var categories = categoryTestData.createCategories(5);
        var categoryIds = categories.stream().map(Category::getId).toList();

        StepVerifier.create(fetcher.checkExistCategories(categoryIds))
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionIfCategoryNotFound() {
        StepVerifier.create(fetcher.checkExistCategories(List.of(new ObjectId())))
                .expectErrorMatches(throwable -> throwable instanceof StoreException &&
                        throwable.getMessage().equals("No categories were found."))
                .verify();
    }

    @Test
    void shouldThrowExceptionForDifferentCollectionSize() {
        var categories = categoryTestData.createCategories(2);
        var categoryIds = categories.stream().map(Category::getId).collect(Collectors.toList());
        categoryIds.add(new ObjectId());

        StepVerifier.create(fetcher.checkExistCategories(categoryIds))
                .expectErrorMatches(throwable -> throwable instanceof StoreException &&
                        throwable.getMessage().equals("Category IDs must have same length."))
                .verify();
    }
}