package org.devcalm.store.manager.data;

import org.devcalm.store.manager.domain.model.Vendor;
import org.devcalm.store.manager.domain.repository.VendorRepository;
import org.instancio.Instancio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.instancio.Select.field;

@Component
public class VendorTestData {

    @Autowired
    private VendorRepository vendorRepository;

    public Vendor createVendor() {
        return vendorRepository.save(Instancio.of(Vendor.class)
                .set(field(Vendor::isArchived), false)
                .ignore(field(Vendor::getCategoryIds))
                .ignore(field(Vendor::getProductIds))
                .ignore(field(Vendor::getStoreIds))
                .create()).block();
    }
}
