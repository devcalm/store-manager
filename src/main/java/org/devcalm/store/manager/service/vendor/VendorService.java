package org.devcalm.store.manager.service.vendor;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.repository.VendorRepository;
import org.devcalm.store.manager.web.dto.SaveVendorRequest;
import org.devcalm.store.manager.web.dto.VendorDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    public Mono<VendorDto> update(ObjectId id, SaveVendorRequest request) {
        return vendorFetcher.findById(id).flatMap(vendor -> {
            vendor.setName(request.name());
            vendor.setDescription(request.description());
            return vendorRepository.save(vendor);
        }).map(vendorMapper::toDto);
    }

    public Mono<Void> delete(ObjectId id) {
        return vendorFetcher.findById(id).flatMap(s -> {
            s.setArchived(true);
            return vendorRepository.save(s);
        }).then();
    }

    public Mono<Page<VendorDto>> findAll(Pageable pageable) {
        return vendorRepository.countByArchivedFalse()
                .zipWith(vendorFetcher.findAllWithRelations(pageable).map(vendorMapper::toDto).collectList())
                .map(tuple -> new PageImpl<>(tuple.getT2(), pageable, tuple.getT1()));
    }

    public Mono<VendorDto> findById(ObjectId id) {
        return vendorFetcher.findByIdWithRelations(id)
                .map(vendorMapper::toDto);
    }
}
