package org.devcalm.store.manager.service.category;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.model.Category;
import org.devcalm.store.manager.web.dto.CategoryDto;
import org.devcalm.store.manager.web.dto.CategoryNode;
import org.devcalm.store.manager.web.dto.SaveCategoryRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
public class CategoryMapper {

    public Category toEntity(SaveCategoryRequest request) {
        return Category.builder()
                .name(request.name())
                .description(request.description())
                .parentId(request.parentId())
                .build();
    }

    public CategoryDto toDto(Category entity) {
        return CategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .parentId(entity.getParentId())
                .updatedAt(entity.getUpdatedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public Flux<CategoryNode> toCategoryNodes(Flux<Category> categories) {
        var nodeMap = new HashMap<ObjectId, CategoryNode>();
        var rootNodes = new TreeSet<>(Comparator.comparing(CategoryNode::getName));

        return categories.flatMap(category -> {
            var node = new CategoryNode(category.getId(), category.getName(), category.getDescription());
            nodeMap.put(category.getId(), node);

            rootNodes.add(node);

            if (category.getParentId() != null) {
                var parent = nodeMap.get(category.getParentId());
                if (parent != null) {
                    parent.addChild(node);
                    rootNodes.remove(node);
                }
            }
            return Mono.just(node);
        }).thenMany(Flux.fromIterable(rootNodes));
    }

}
