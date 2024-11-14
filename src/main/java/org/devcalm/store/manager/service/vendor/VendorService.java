package org.devcalm.store.manager.service.vendor;

import lombok.RequiredArgsConstructor;
import org.devcalm.store.manager.domain.repository.VendorRepository;
import org.devcalm.store.manager.web.dto.CreateVendorRequest;
import org.devcalm.store.manager.web.dto.VendorDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class VendorService {

    private final VendorMapper vendorMapper;
    private final VendorRepository vendorRepository;

    public Mono<VendorDto> create(CreateVendorRequest request) {
        return vendorRepository.save(vendorMapper.toEntity(request))
                .map(vendorMapper::toDto);
    }
}
