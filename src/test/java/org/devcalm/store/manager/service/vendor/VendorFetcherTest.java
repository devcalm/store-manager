package org.devcalm.store.manager.service.vendor;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.MongoTestConfig;
import org.devcalm.store.manager.data.VendorTestData;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.infrastructure.reflection.FieldNamesExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

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
}
