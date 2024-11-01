package org.devcalm.store.manager.web.api;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.service.category.CategoryService;
import org.devcalm.store.manager.web.dto.CategoryDto;
import org.devcalm.store.manager.web.dto.CategoryNode;
import org.devcalm.store.manager.web.dto.SaveCategoryRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public Mono<CategoryDto> create(@RequestBody @Validated SaveCategoryRequest request) {
        return categoryService.create(request);
    }

    @GetMapping("{id}")
    public Mono<CategoryDto> find(@PathVariable ObjectId id) {
        return categoryService.find(id);
    }

    @PutMapping("{id}")
    public Mono<CategoryDto> update(@RequestBody @Validated SaveCategoryRequest request, @PathVariable ObjectId id) {
        return categoryService.update(id, request);
    }

    @DeleteMapping({"{id}"})
    public Mono<Void> delete(@PathVariable ObjectId id) {
        return categoryService.delete(id);
    }

    @GetMapping("hierarchy")
    public Flux<CategoryNode> hierarchy() {
        return categoryService.fetchHierarchy();
    }
}
