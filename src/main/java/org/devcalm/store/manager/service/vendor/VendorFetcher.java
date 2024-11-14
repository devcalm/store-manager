package org.devcalm.store.manager.service.vendor;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.model.Vendor;
import org.devcalm.store.manager.domain.repository.VendorRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class VendorFetcher {

    private final VendorRepository vendorRepository;

    public Mono<Vendor> findById(ObjectId id) {
        return vendorRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Vendor %s is not found.".formatted(id))));
    }
}
