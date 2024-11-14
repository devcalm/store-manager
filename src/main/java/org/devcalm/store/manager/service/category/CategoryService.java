package org.devcalm.store.manager.service.category;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.model.Category;
import org.devcalm.store.manager.domain.model.Vendor;
import org.devcalm.store.manager.domain.repository.CategoryRepository;
import org.devcalm.store.manager.web.dto.CategoryDto;
import org.devcalm.store.manager.web.dto.CategoryNode;
import org.devcalm.store.manager.web.dto.SaveCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryFetcher categoryFetcher;
    private final CategoryRepository categoryRepository;
    private final CategoryWriteModel categoryWriteModel;

    public Mono<CategoryDto> create(Vendor vendor, SaveCategoryRequest request) {
        return extractParentCategory(request)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .flatMap(parent -> {
                    var category = categoryMapper.toEntity(request);
                    category.setVendorId(vendor.getId());
                    return categoryRepository.save(category);
                }).map(categoryMapper::toDto);
    }

    public Mono<CategoryDto> find(ObjectId id) {
        return categoryFetcher.findById(id)
                .map(categoryMapper::toDto);
    }

    public Mono<Page<CategoryDto>> findAll(Pageable pageable) {
        return categoryRepository.countByArchivedFalse()
                .zipWith(categoryRepository.findAllByArchivedFalse(pageable).map(categoryMapper::toDto).collectList())
                .map(tuple -> new PageImpl<>(tuple.getT2(), pageable, tuple.getT1()));
    }

    public Mono<CategoryDto> update(ObjectId id, SaveCategoryRequest request) {
        var categoryMono = categoryFetcher.findById(id);
        var parentMono = extractParentCategory(request)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty());

        return Mono.zip(categoryMono, parentMono).flatMap(tuple -> {
            var category = tuple.getT1();
            category.setName(request.name());
            category.setDescription(request.description());
            category.setParentId(request.parentId());
            return categoryRepository.save(category);
        }).map(categoryMapper::toDto);
    }

    private Mono<Category> extractParentCategory(SaveCategoryRequest request) {
        return request.hasParent()
                ? categoryRepository.findById(request.parentId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Parent category is not found.")))
                : Mono.empty();
    }

    @Transactional
    public Mono<Void> delete(ObjectId id) {
        return categoryFetcher.findById(id)
                .flatMap(category -> {
                    category.setArchived(true);
                    return categoryRepository.save(category);
                }).then(categoryWriteModel.archiveCategories(
                        categoryFetcher.fetchDescendants(id)
                ));
    }

    public Flux<CategoryNode> fetchHierarchy() {
        return categoryMapper.toCategoryNodes(categoryRepository.findAllByArchivedFalse());
    }
}
