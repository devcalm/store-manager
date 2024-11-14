package org.devcalm.store.manager.service.product;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.MongoTestConfig;
import org.devcalm.store.manager.data.ProductTestData;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

@DataMongoTest
@ContextConfiguration(classes = {MongoTestConfig.class})
@ComponentScan(basePackageClasses = {ProductFetcher.class, ProductTestData.class},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ProductService.class)})
class ProductFetcherTest {

    @Autowired
    private ProductFetcher productFetcher;
    @Autowired
    private ProductTestData productTestData;

    @Test
    void shouldFindById() {
        var product = productTestData.createProduct();

        StepVerifier.create(productFetcher.findById(product.getId()))
                .expectNext(product).verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        var id = new ObjectId();
        StepVerifier.create(productFetcher.findById(id))
                .expectErrorMatches(throwable -> throwable instanceof EntityNotFoundException &&
                        throwable.getMessage().equals("Product %s is not found.".formatted(id)))
                .verify();
    }
}