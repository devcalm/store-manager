package org.devcalm.store.manager.service.product;

import org.devcalm.store.manager.MongoTestConfig;

import org.devcalm.store.manager.data.CategoryTestDataService;
import org.devcalm.store.manager.data.ProductTestDataService;
import org.devcalm.store.manager.domain.model.BaseEntity;
import org.devcalm.store.manager.domain.repository.ProductRepository;
import org.devcalm.store.manager.service.category.CategoryFetcher;
import org.devcalm.store.manager.web.dto.ProductDto;
import org.devcalm.store.manager.web.dto.SaveProductRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@DataMongoTest
@ContextConfiguration(classes = MongoTestConfig.class)
@ComponentScan(basePackageClasses = {ProductService.class, CategoryFetcher.class, ProductTestDataService.class, CategoryTestDataService.class})
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryTestDataService categoryTestDataService;
    @Autowired
    private ProductTestDataService productTestDataService;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll().block();
    }

    @Test
    void shouldCreateProduct() {
        var categoryId = categoryTestDataService.createCategory().getId();
        var request = Instancio.of(SaveProductRequest.class)
                .set(field(SaveProductRequest::categories), List.of(categoryId))
                .create();

        StepVerifier.create(productService.create(request)).expectNextMatches(dto -> {
            assertProductDto(dto, request);
            return true;
        }).verifyComplete();
    }

    @Test
    void shouldFindById() {
        var store = productTestDataService.createProduct();

        StepVerifier.create(productService.findById(store.getId()))
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFindAllProducts() {
        final int sizeOfElements = 5;
        var stores = productTestDataService.createProducts(sizeOfElements);

        assertThat(stores).isNotEmpty();

        StepVerifier.create(productService.findAll(Pageable.unpaged()))
                .expectNextMatches(page -> {
                    assertThat(page).isNotNull();
                    assertThat(page.getTotalElements()).isEqualTo(sizeOfElements);
                    return true;
                }).verifyComplete();
    }

    @Test
    void shouldSoftDelete() {
        var store = productTestDataService.createProduct();

        StepVerifier.create(productService.delete(store.getId())
                        .then(productRepository.findById(store.getId())))
                .expectNextMatches(BaseEntity::isArchived)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldUpdateProduct() {
        var categoryId = categoryTestDataService.createCategory().getId();
        var store = productTestDataService.createProduct();
        var request = Instancio.of(SaveProductRequest.class)
                .set(field(SaveProductRequest::categories), List.of(categoryId))
                .create();

        StepVerifier.create(productService.update(store.getId(), request)).expectNextMatches(dto -> {
            assertProductDto(dto, request);
            return true;
        }).verifyComplete();
    }

    private void assertProductDto(ProductDto dto, SaveProductRequest request) {
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo(request.name());
        assertThat(dto.getDescription()).isEqualTo(request.description());
        assertThat(dto.getDiscount()).isEqualTo(request.discount());
        assertThat(dto.getPrice()).isEqualTo(request.price());
        assertThat(dto.getNotes()).isEqualTo(request.notes());
        assertThat(dto.getTimeTakenInMinutes()).isEqualTo(request.timeTakenInMinutes());
        assertThat(dto.getCategories()).isEqualTo(request.categories());
    }
}