package org.devcalm.store.manager.data;

import org.devcalm.store.manager.domain.model.Vendor;
import org.devcalm.store.manager.domain.repository.VendorRepository;
import org.instancio.Instancio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@Component
public class VendorTestData {

    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private CategoryTestData categoryTestData;
    @Autowired
    private ProductTestData productTestData;
    @Autowired
    private StoreTestData storeTestData;

    public Vendor createVendor() {
        return vendorRepository.save(Instancio.of(Vendor.class)
                .set(field(Vendor::isArchived), false)
                .ignore(field(Vendor::getCategoryIds))
                .ignore(field(Vendor::getProductIds))
                .ignore(field(Vendor::getStoreIds))
                .create()).block();
    }

    public List<Vendor> createVendors(int sizeOfElements) {
        var entities = Instancio.ofList(Vendor.class)
                .size(sizeOfElements)
                .set(field(Vendor::isArchived), false)
                .create();
        return vendorRepository.saveAll(entities).collectList().block();
    }

    public Vendor createVendorWithRelations() {
        var vendor = createVendor();
        assertThat(categoryTestData.createCategory(vendor.getId())).isNotNull();
        assertThat(productTestData.createProduct(vendor.getId())).isNotNull();
        assertThat(storeTestData.createStore(vendor.getId())).isNotNull();
        return vendor;
    }

}
