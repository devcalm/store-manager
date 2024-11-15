package org.devcalm.store.manager.service.store;

import org.devcalm.store.manager.MongoTestConfig;
import org.devcalm.store.manager.data.CategoryTestData;
import org.devcalm.store.manager.data.ProductTestData;
import org.devcalm.store.manager.data.StoreTestData;
import org.devcalm.store.manager.domain.model.BaseEntity;
import org.devcalm.store.manager.domain.model.ProductCustomization;
import org.devcalm.store.manager.domain.model.Vendor;
import org.devcalm.store.manager.domain.repository.StoreRepository;
import org.devcalm.store.manager.service.category.CategoryFetcher;
import org.devcalm.store.manager.web.dto.SaveStoreRequest;
import org.devcalm.store.manager.web.dto.StoreDto;
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
@ComponentScan(basePackageClasses = {StoreService.class, StoreTestData.class, CategoryFetcher.class, CategoryTestData.class, ProductTestData.class})
class StoreServiceTest {

    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private StoreTestData storeTestData;
    @Autowired
    private ProductTestData productTestData;
    @Autowired
    private CategoryTestData categoryTestData;

    @BeforeEach
    void setUp() {
        storeRepository.deleteAll().block();
    }

    @Test
    void shouldCreateStore() {
        var categoryId = categoryTestData.createCategory().getId();
        var productId = productTestData.createProduct().getId();
        var productCustomization = Instancio.of(ProductCustomization.class)
                .set(field(ProductCustomization::getProductId), productId)
                .create();
        var vendor = Instancio.of(Vendor.class).create();
        var request = Instancio.of(SaveStoreRequest.class)
                .set(field(SaveStoreRequest::categories), List.of(categoryId))
                .set(field(SaveStoreRequest::products), List.of(productCustomization))
                .create();

        StepVerifier.create(storeService.create(vendor, request)).expectNextMatches(dto -> {
            assertStoreDto(dto, request);
            return true;
        }).verifyComplete();
    }

    @Test
    void shouldUpdateStore() {
        var categoryId = categoryTestData.createCategory().getId();
        var store = storeTestData.createStore();
        var request = Instancio.of(SaveStoreRequest.class)
                .set(field(SaveStoreRequest::categories), List.of(categoryId))
                .ignore(field(SaveStoreRequest::products))
                .create();

        StepVerifier.create(storeService.update(store.getId(), request)).expectNextMatches(dto -> {
            assertStoreDto(dto, request);
            return true;
        }).verifyComplete();
    }

    @Test
    void shouldSoftDeleteStore() {
        var store = storeTestData.createStore();

        StepVerifier.create(storeService.delete(store.getId())
                        .then(storeRepository.findById(store.getId())))
                .expectNextMatches(BaseEntity::isArchived)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFindStore() {
        var store = storeTestData.createStore();

        StepVerifier.create(storeService.findById(store.getId()))
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFindAllStores() {
        final int sizeOfElements = 5;
        var stores = storeTestData.createStores(sizeOfElements);

        assertThat(stores).isNotEmpty();

        StepVerifier.create(storeService.findAll(Pageable.unpaged()))
                .expectNextMatches(page -> {
                    assertThat(page).isNotNull();
                    assertThat(page.getTotalElements()).isEqualTo(sizeOfElements);
                    return true;
                }).verifyComplete();
    }

    private void assertStoreDto(StoreDto dto, SaveStoreRequest request) {
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo(request.name());
        assertThat(dto.getDescription()).isEqualTo(request.description());
        assertThat(dto.getAddressInfo()).isEqualTo(request.addressInfo());
        assertThat(dto.getContactInfo()).isEqualTo(request.contactInfo());
        assertThat(dto.getCategories()).isEqualTo(request.categories());
        assertThat(dto.getTags()).isEqualTo(request.tags());
        assertThat(dto.getProducts()).isEqualTo(request.products());
    }
}
