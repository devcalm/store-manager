package org.devcalm.store.manager.service.store;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.model.Vendor;
import org.devcalm.store.manager.domain.repository.StoreRepository;
import org.devcalm.store.manager.service.category.CategoryFetcher;
import org.devcalm.store.manager.web.dto.SaveStoreRequest;
import org.devcalm.store.manager.web.dto.StoreDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class StoreService {

    private final StoreMapper storeMapper;
    private final StoreFetcher storeFetcher;
    private final CategoryFetcher categoryFetcher;
    private final StoreRepository storeRepository;

    public Mono<StoreDto> create(Vendor vendor, SaveStoreRequest request) {
        return categoryFetcher.checkExistCategories(request.categories())
                .then(Mono.defer(() -> {
                            var store = storeMapper.toEntity(request);
                            store.setVendorId(vendor.getId());
                            return storeRepository.save(store);
                        }
                ).map(storeMapper::toDto));
    }

    public Mono<StoreDto> findById(ObjectId id) {
        return storeFetcher.findById(id)
                .map(storeMapper::toDto);
    }

    public Mono<Page<StoreDto>> findAll(Pageable pageable) {
        return storeRepository.countByArchivedFalse()
                .zipWith(storeRepository.findAllByArchivedFalse(pageable).map(storeMapper::toDto).collectList())
                .map(tuple -> new PageImpl<>(tuple.getT2(), pageable, tuple.getT1()));
    }

    public Mono<Void> delete(ObjectId id) {
        return storeFetcher.findById(id).flatMap(s -> {
            s.setArchived(true);
            return storeRepository.save(s);
        }).then();
    }

    public Mono<StoreDto> update(ObjectId id, SaveStoreRequest request) {
        return categoryFetcher.checkExistCategories(request.categories())
                .then(Mono.defer(() -> storeFetcher.findById(id))
                        .flatMap(store -> {
                            store.setName(request.name());
                            store.setDescription(request.description());
                            store.setTags(request.tags());
                            store.setAddressInfo(request.addressInfo());
                            store.setContactInfo(request.contactInfo());
                            store.setCategoryIds(request.categories());
                            return storeRepository.save(store);
                        }).map(storeMapper::toDto));
    }
}
