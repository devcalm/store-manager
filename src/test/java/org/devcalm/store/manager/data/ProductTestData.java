package org.devcalm.store.manager.data;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.model.Product;
import org.devcalm.store.manager.domain.repository.ProductRepository;
import org.instancio.Instancio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.instancio.Select.field;

@Component
public class ProductTestData {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct() {
        return productRepository.save(Instancio.of(Product.class)
                .set(field(Product::isArchived), false)
                .create()
        ).block();
    }

    public Product createProduct(ObjectId vendorId) {
        return productRepository.save(Instancio.of(Product.class)
                .set(field(Product::isArchived), false)
                .set(field(Product::getVendorId), vendorId)
                .create()
        ).block();
    }

    public List<Product> createProducts(int sizeOfElements) {
        var entities = Instancio.ofList(Product.class)
                .size(sizeOfElements)
                .set(field(Product::isArchived), false)
                .create();
        return productRepository.saveAll(entities).collectList().block();
    }
}
