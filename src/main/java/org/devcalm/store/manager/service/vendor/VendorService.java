package org.devcalm.store.manager.service.vendor;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.repository.VendorRepository;
import org.devcalm.store.manager.web.dto.SaveVendorRequest;
import org.devcalm.store.manager.web.dto.VendorDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class VendorService {

    private final VendorMapper vendorMapper;
    private final VendorFetcher vendorFetcher;
    private final VendorRepository vendorRepository;

    public Mono<VendorDto> create(SaveVendorRequest request) {
        return vendorRepository.save(vendorMapper.toEntity(request))
                .map(vendorMapper::toDto);
    }

    public Mono<VendorDto> findById(ObjectId id) {
        return vendorFetcher.findByIdWithRelations(id)
                .map(vendorMapper::toDto);
    }
}
