package org.devcalm.store.manager.web.api;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.service.product.ProductService;
import org.devcalm.store.manager.service.vendor.VendorFetcher;
import org.devcalm.store.manager.web.dto.SaveProductRequest;
import org.devcalm.store.manager.web.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/product/{vendorId}")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final VendorFetcher vendorFetcher;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductDto> create(@PathVariable ObjectId vendorId, @RequestBody @Validated SaveProductRequest dto) {
        return vendorFetcher.findById(vendorId)
                .flatMap(vendor -> productService.create(vendor, dto));
    }

    @PutMapping("{id}")
    public Mono<ProductDto> create(@PathVariable ObjectId vendorId, @RequestBody @Validated SaveProductRequest dto, @PathVariable ObjectId id) {
        return vendorFetcher.findById(vendorId)
                .then(productService.update(id, dto));
    }

    @GetMapping("{id}")
    public Mono<ProductDto> find(@PathVariable ObjectId vendorId, @PathVariable ObjectId id) {
        return vendorFetcher.findById(vendorId)
                .then(productService.findById(id));
    }

    @GetMapping
    public Mono<Page<ProductDto>> findAll(@PathVariable ObjectId vendorId,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "25") int size,
                                          @RequestParam(defaultValue = "ID") SortField sortField,
                                          @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        var pageable = PageRequest.of(page, size, sortDirection, sortField.getDatabaseFileName());
        return vendorFetcher.findById(vendorId)
                .then(productService.findAll(pageable));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable ObjectId vendorId, @PathVariable ObjectId id) {
        return vendorFetcher.findById(vendorId)
                .then(productService.delete(id));
    }
}
