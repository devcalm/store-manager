package org.devcalm.store.manager.service.category;

import org.devcalm.store.manager.MongoTestConfig;
import org.devcalm.store.manager.data.CategoryTestDataService;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.model.Category;
import org.devcalm.store.manager.domain.repository.CategoryRepository;
import org.devcalm.store.manager.web.dto.CategoryDto;
import org.devcalm.store.manager.web.dto.SaveCategoryRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

@DataMongoTest
@ContextConfiguration(classes = MongoTestConfig.class)
@ComponentScan(basePackageClasses = {CategoryService.class, CategoryTestDataService.class})
class CategoryServiceTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryTestDataService categoryTestDataService;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll().block();
    }

    @Test
    void shouldCreateCategoryWithoutParent() {
        var request = createRequestWithoutParent();

        StepVerifier.create(categoryService.create(request))
                .expectNextMatches(dto -> {
                    assertCategoryDto(dto, request);
                    return true;
                }).verifyComplete();
    }

    @Test
    void shouldCreateCategoryWithParent() {
        var requestWithoutParent = createRequestWithoutParent();
        var parentDto = categoryService.create(requestWithoutParent).block();

        assertThat(parentDto).isNotNull();

        var request = Instancio.of(SaveCategoryRequest.class)
                .set(field(SaveCategoryRequest::parentId), parentDto.getId())
                .create();

        StepVerifier.create(categoryService.create(request))
                .expectNextMatches(dto -> {
                    assertCategoryDto(dto, request);
                    return true;
                }).verifyComplete();
    }

    private SaveCategoryRequest createRequestWithoutParent() {
        return Instancio.of(SaveCategoryRequest.class)
                .ignore(field(SaveCategoryRequest::parentId))
                .create();
    }

    @Test
    void shouldThrowExceptionWhenParentCategoryNotFound() {
        var request = Instancio.of(SaveCategoryRequest.class).create();

        StepVerifier.create(categoryService.create(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof EntityNotFoundException &&
                                throwable.getMessage().equals("Parent category is not found."))
                .verify();
    }

    @Test
    void shouldFindNotArchivedCategory() {
        var category = categoryTestDataService.createCategory();

        StepVerifier.create(categoryService.find(category.getId()))
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void shouldFindAllNotArchivedCategory() {
        final int sizeOfElements = 5;
        var categories = categoryTestDataService.createCategories(sizeOfElements);

        assertThat(categories).isNotEmpty();

        StepVerifier.create(categoryService.findAll(Pageable.unpaged()))
                .expectNextMatches(page -> {
                    assertThat(page).isNotNull();
                    assertThat(page.getTotalElements()).isEqualTo(sizeOfElements);
                    return true;
                }).verifyComplete();
    }

    @Test
    void shouldUpdateCategory() {
        var category = categoryTestDataService.createCategory();
        var request = createRequestWithoutParent();

        StepVerifier.create(categoryService.update(category.getId(), request))
                .expectNextMatches(dto -> {
                    assertCategoryDto(dto, request);
                    return true;
                }).verifyComplete();
    }

    @Test
    void shouldDeleteCategoriesWithDescendants() {
        var category = categoryTestDataService.createCategoryWithParent();

        categoryService.delete(category.getParentId()).block();

        StepVerifier.create(categoryRepository.findAllById(List.of(category.getId(), category.getParentId())))
                .thenConsumeWhile(Category::isArchived)
                .verifyComplete();
    }

    @Test
    void shouldFetchHierarchy() {
        final int sizeOfElements = 10;
        categoryTestDataService.createCategories(sizeOfElements);

        StepVerifier.create(categoryService.fetchHierarchy())
                .expectNextCount(sizeOfElements)
                .verifyComplete();
    }

    private void assertCategoryDto(CategoryDto dto, SaveCategoryRequest request) {
        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getName()).isEqualTo(request.name());
        assertThat(dto.getDescription()).isEqualTo(request.description());
        assertThat(dto.getParentId()).isEqualTo(request.parentId());
    }
}