package org.devcalm.store.manager.service.vendor;

import org.devcalm.store.manager.MongoTestConfig;
import org.devcalm.store.manager.data.VendorTestData;
import org.devcalm.store.manager.domain.model.BaseEntity;
import org.devcalm.store.manager.domain.repository.VendorRepository;
import org.devcalm.store.manager.infrastructure.reflection.FieldNamesExtractor;
import org.devcalm.store.manager.web.dto.SaveVendorRequest;
import org.devcalm.store.manager.web.dto.VendorDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ContextConfiguration(classes = MongoTestConfig.class)
@ComponentScan(basePackageClasses = {VendorService.class, VendorFetcher.class, VendorTestData.class, FieldNamesExtractor.class})
class VendorServiceTest {

    @Autowired
    private VendorService vendorService;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private VendorTestData vendorTestData;

    @BeforeEach
    void setUp() {
        vendorRepository.deleteAll().block();
    }

    @Test
    void shouldCreateVendor() {
        var request = Instancio.of(SaveVendorRequest.class).create();

        StepVerifier.create(vendorService.create(request))
                .expectNextMatches(dto -> {
                    assertVendorDto(dto, request);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldUpdateVendor() {
        var vendor = vendorTestData.createVendor();
        var request = Instancio.of(SaveVendorRequest.class).create();

        StepVerifier.create(vendorService.update(vendor.getId(), request))
                .expectNextMatches(dto -> {
                    assertVendorDto(dto, request);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldFindById() {
        var vendor = vendorTestData.createVendorWithRelations();

        StepVerifier.create(vendorService.findById(vendor.getId()))
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void shouldDeleteVendor() {
        var vendor = vendorTestData.createVendor();

        StepVerifier.create(vendorService.delete(vendor.getId())
                        .then(vendorRepository.findById(vendor.getId())))
                .expectNextMatches(BaseEntity::isArchived)
                .expectComplete()
                .verify();
    }

    @Test
    void shouldFindAllVendors() {
        final int sizeOfElements = 5;
        var vendors = vendorTestData.createVendors(sizeOfElements);

        assertThat(vendors).isNotEmpty();

        StepVerifier.create(vendorService.findAll(Pageable.ofSize(1)))
                .expectNextMatches(page -> {
                    assertThat(page).isNotNull();
                    assertThat(page.getTotalElements()).isEqualTo(sizeOfElements);
                    return true;
                }).verifyComplete();
    }

    private void assertVendorDto(VendorDto dto, SaveVendorRequest request) {
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getName()).isEqualTo(request.name());
        assertThat(dto.getDescription()).isEqualTo(request.description());
    }
}