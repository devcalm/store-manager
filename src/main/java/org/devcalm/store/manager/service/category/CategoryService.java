package org.devcalm.store.manager.service.category;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.model.Category;
import org.devcalm.store.manager.domain.repository.CategoryRepository;
import org.devcalm.store.manager.web.dto.CategoryDto;
import org.devcalm.store.manager.web.dto.CategoryNode;
import org.devcalm.store.manager.web.dto.SaveCategoryRequest;
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
    private final CategoryRepositorySupport categoryRepositorySupport;

    public Mono<CategoryDto> create(SaveCategoryRequest request) {
        return extractParentCategory(request)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .flatMap(parent -> {
                    var category = categoryMapper.toEntity(request);
                    category.setParent(parent.orElse(null));
                    return categoryRepository.save(category);
                }).map(categoryMapper::toDto);
    }

    public Mono<CategoryDto> find(ObjectId id) {
        return categoryFetcher.findById(id)
                .map(categoryMapper::toDto);
    }

    public Mono<CategoryDto> update(ObjectId id, SaveCategoryRequest request) {
        var categoryMono = categoryFetcher.findById(id);
        var parentMono = extractParentCategory(request)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty());

        return Mono.zip(categoryMono, parentMono).flatMap(tuple -> {
            var category = tuple.getT1();
            var parent = tuple.getT2();

            category.setName(request.name());
            category.setDescription(request.description());
            category.setParentId(request.parentId());
            category.setParent(parent.orElse(null));
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
                }).then(categoryRepositorySupport.archiveCategories(
                        categoryFetcher.fetchDescendants(id)
                ));
    }

    public Flux<CategoryNode> fetchHierarchy() {
        return categoryMapper.toCategoryNodes(categoryRepository.findByArchivedFalse());
    }
}
