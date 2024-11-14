package org.devcalm.store.manager.web.api;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.service.category.CategoryService;
import org.devcalm.store.manager.service.vendor.VendorFetcher;
import org.devcalm.store.manager.web.dto.CategoryDto;
import org.devcalm.store.manager.web.dto.CategoryNode;
import org.devcalm.store.manager.web.dto.SaveCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/category/{vendorId}")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final VendorFetcher vendorFetcher;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CategoryDto> create(@PathVariable ObjectId vendorId, @RequestBody @Validated SaveCategoryRequest request) {
        return vendorFetcher.findById(vendorId)
                .flatMap(vendor -> categoryService.create(vendor, request));
    }

    @GetMapping("{id}")
    public Mono<CategoryDto> find(@PathVariable ObjectId vendorId, @PathVariable ObjectId id) {
        return vendorFetcher.findById(vendorId)
                .then(categoryService.find(id));
    }

    @GetMapping
    public Mono<Page<CategoryDto>> findAll(@PathVariable ObjectId vendorId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "25") int size,
                                           @RequestParam(defaultValue = "ID") SortField sortField,
                                           @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        var pageable = PageRequest.of(page, size, sortDirection, sortField.getDatabaseFileName());
        return vendorFetcher.findById(vendorId)
                .then(categoryService.findAll(pageable));
    }

    @PutMapping("{id}")
    public Mono<CategoryDto> update(@PathVariable ObjectId vendorId, @RequestBody @Validated SaveCategoryRequest request, @PathVariable ObjectId id) {
        return vendorFetcher.findById(vendorId)
                .then(categoryService.update(id, request));
    }

    @DeleteMapping({"{id}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable ObjectId vendorId, @PathVariable ObjectId id) {
        return vendorFetcher.findById(vendorId)
                .then(categoryService.delete(id));

    }

    @GetMapping("hierarchy")
    public Flux<CategoryNode> hierarchy(@PathVariable ObjectId vendorId) {
        return vendorFetcher.findById(vendorId)
                .thenMany(categoryService.fetchHierarchy());
    }
}
