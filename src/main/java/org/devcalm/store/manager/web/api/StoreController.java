package org.devcalm.store.manager.web.api;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.service.store.StoreService;
import org.devcalm.store.manager.service.vendor.VendorFetcher;
import org.devcalm.store.manager.web.dto.SaveStoreRequest;
import org.devcalm.store.manager.web.dto.StoreDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/store/{vendorId}")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final VendorFetcher vendorFetcher;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<StoreDto> create(@PathVariable ObjectId vendorId, @RequestBody @Validated SaveStoreRequest dto) {
        return vendorFetcher.findById(vendorId)
                .flatMap(vendor -> storeService.create(vendor, dto));
    }

    @PutMapping("{id}")
    public Mono<StoreDto> create(@PathVariable ObjectId vendorId, @RequestBody @Validated SaveStoreRequest dto, @PathVariable ObjectId id) {
        return vendorFetcher.findById(vendorId)
                .then(storeService.update(id, dto));
    }

    @GetMapping("{id}")
    public Mono<StoreDto> find(@PathVariable ObjectId vendorId, @PathVariable ObjectId id) {
        return vendorFetcher.findById(vendorId)
                .then(storeService.findById(id));
    }

    @GetMapping
    public Mono<Page<StoreDto>> findAll(@PathVariable ObjectId vendorId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "25") int size,
                                        @RequestParam(defaultValue = "ID") SortField sortField,
                                        @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        var pageable = PageRequest.of(page, size, sortDirection, sortField.getDatabaseFileName());
        return vendorFetcher.findById(vendorId)
                .then(storeService.findAll(pageable));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable ObjectId vendorId, @PathVariable ObjectId id) {
        return vendorFetcher.findById(vendorId)
                .then(storeService.delete(id));
    }
}
