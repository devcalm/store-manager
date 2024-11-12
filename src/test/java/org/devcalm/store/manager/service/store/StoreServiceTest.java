package org.devcalm.store.manager.service.store;

import org.devcalm.store.manager.MongoTestConfig;
import org.devcalm.store.manager.data.CategoryTestDataService;
import org.devcalm.store.manager.data.StoreTestDataService;
import org.devcalm.store.manager.domain.model.BaseEntity;
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
@ComponentScan(basePackageClasses = {StoreService.class, StoreTestDataService.class, CategoryFetcher.class, CategoryTestDataService.class})
class StoreServiceTest {

    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private StoreTestDataService storeTestDataService;
    @Autowired
    private CategoryTestDataService categoryTestDataService;

    @BeforeEach
    void setUp() {
        storeRepository.deleteAll().block();
    }

    @Test
    void shouldCreateStore() {
        var categoryId = categoryTestDataService.createCategory().getId();
        var request = Instancio.of(SaveStoreRequest.class)
                .set(field(SaveStoreRequest::categories), List.of(categoryId))
                .create();

        StepVerifier.create(storeService.create(request)).expectNextMatches(dto -> {
            assertStoreDto(dto, request);
            return true;
        }).verifyComplete();
    }

    @Test
    void shouldUpdateStore() {
        var categoryId = categoryTestDataService.createCategory().getId();
        var store = storeTestDataService.createStore();
        var request = Instancio.of(SaveStoreRequest.class)
                .set(field(SaveStoreRequest::categories), List.of(categoryId))
                .create();

        StepVerifier.create(storeService.update(store.getId(), request)).expectNextMatches(dto -> {
            assertStoreDto(dto, request);
            return true;
        }).verifyComplete();
    }

    @Test
    void shouldSoftDeleteStore() {
        var store = storeTestDataService.createStore();

        StepVerifier.create(storeService.delete(store.getId())
                        .then(storeRepository.findById(store.getId())))
                .expectNextMatches(BaseEntity::isArchived)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFindStore() {
        var store = storeTestDataService.createStore();

        StepVerifier.create(storeService.findById(store.getId()))
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFindAllStores() {
        final int sizeOfElements = 5;
        var stores = storeTestDataService.createStores(sizeOfElements);

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
    }
}
