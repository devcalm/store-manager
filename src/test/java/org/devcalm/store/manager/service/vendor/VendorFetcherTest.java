package org.devcalm.store.manager.service.vendor;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.MongoTestConfig;
import org.devcalm.store.manager.data.VendorTestData;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.model.Vendor;
import org.devcalm.store.manager.domain.repository.VendorRepository;
import org.devcalm.store.manager.infrastructure.reflection.FieldNamesExtractor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.util.function.Predicate;

@DataMongoTest
@ContextConfiguration(classes = MongoTestConfig.class)
@ComponentScan(basePackageClasses = {VendorFetcher.class, VendorTestData.class, FieldNamesExtractor.class},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = VendorService.class)}
)
class VendorFetcherTest {

    @Autowired
    private VendorTestData testData;
    @Autowired
    private VendorFetcher fetcher;
    @Autowired
    private VendorRepository vendorRepository;

    @BeforeEach
    void setUp() {
        vendorRepository.deleteAll().block();
    }

    @Test
    void shouldFindById() {
        var vendor = testData.createVendor();

        StepVerifier.create(fetcher.findById(vendor.getId()))
                .expectNext(vendor)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenVendorNotFound() {
        var id = new ObjectId();

        StepVerifier.create(fetcher.findById(id))
                .expectErrorMatches(throwable -> throwable instanceof EntityNotFoundException &&
                        throwable.getMessage().equals("Vendor %s is not found.".formatted(id)))
                .verify();
    }

    @Test
    void shouldFindByIdWithRelations() {
        var vendor = testData.createVendorWithRelations();

        Predicate<Vendor> condition = getVendorPredicate(vendor);

        StepVerifier.create(fetcher.findByIdWithRelations(vendor.getId()))
                .expectNextMatches(condition)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenVendorNotFoundWithRelations() {
        var id = new ObjectId();

        StepVerifier.create(fetcher.findByIdWithRelations(id))
                .expectErrorMatches(throwable -> throwable instanceof EntityNotFoundException &&
                        throwable.getMessage().equals("Vendor %s is not found.".formatted(id)))
                .verify();
    }

    @Test
    void shouldFindAllWithRelations() {
        var vendor = testData.createVendorWithRelations();

        Predicate<Vendor> condition = getVendorPredicate(vendor);

        StepVerifier.create(fetcher.findAllWithRelations(Pageable.ofSize(1)))
                .expectNextMatches(condition)
                .verifyComplete();
    }

    private static @NotNull Predicate<Vendor> getVendorPredicate(Vendor vendor) {
        return v -> v.getId().equals(vendor.getId())
                && !v.getStoreIds().isEmpty()
                && !v.getCategoryIds().isEmpty()
                && !v.getProductIds().isEmpty();
    }
}
