package org.devcalm.store.manager.service.category;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.model.Category;
import org.devcalm.store.manager.web.dto.CategoryNode;
import org.devcalm.store.manager.web.dto.SaveCategoryRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {

    private final CategoryMapper mapper = new CategoryMapper();

    @Test
    void toEntity() {
        var request = Instancio.of(SaveCategoryRequest.class).create();
        var entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(request.name());
        assertThat(entity.getDescription()).isEqualTo(request.description());
        assertThat(entity.getParentId()).isEqualTo(request.parentId());
    }

    @Test
    void toDto() {
        var entity = Instancio.of(Category.class).create();
        var dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getName()).isEqualTo(entity.getName());
        assertThat(dto.getDescription()).isEqualTo(entity.getDescription());
        assertThat(dto.getParentId()).isEqualTo(entity.getParentId());
        assertThat(dto.getUpdatedAt()).isEqualTo(entity.getUpdatedAt());
        assertThat(dto.getCreatedAt()).isEqualTo(entity.getCreatedAt());
    }

    @Test
    void toCategoryNodes() {
        var rootId = new ObjectId();
        var childId1 = new ObjectId();
        var childId2 = new ObjectId();

        var rootCategory = Category.builder()
                .id(rootId)
                .name("Root")
                .description("Root Description")
                .parentId(null)
                .build();

        var childCategory1 = Category.builder()
                .id(childId1)
                .name("Child 1")
                .description("Child 1 Description")
                .parentId(rootId)
                .build();

        var childCategory2 = Category.builder()
                .id(childId2)
                .name("Child 2")
                .description("Child 2 Description")
                .parentId(rootId)
                .build();

        Flux<Category> categories = Flux.just(rootCategory, childCategory1, childCategory2);

        StepVerifier.create(mapper.toCategoryNodes(categories))
                .assertNext(rootNode -> {
                    /* Check root node details */
                    assertThat(rootNode.getId()).isEqualTo(rootId);
                    assertThat(rootNode.getName()).isEqualTo("Root");
                    assertThat(rootNode.getDescription()).isEqualTo("Root Description");

                    /* Check children in root node */
                    Set<CategoryNode> children = rootNode.getChildren();
                    assertThat(children).hasSize(2);

                    /* Verify child nodes */
                    CategoryNode childNode1 = children.stream().filter(node -> node.getId().equals(childId1)).findFirst().orElseThrow();
                    assertThat(childNode1.getName()).isEqualTo("Child 1");
                    assertThat(childNode1.getDescription()).isEqualTo("Child 1 Description");

                    CategoryNode childNode2 = children.stream().filter(node -> node.getId().equals(childId2)).findFirst().orElseThrow();
                    assertThat(childNode2.getName()).isEqualTo("Child 2");
                    assertThat(childNode2.getDescription()).isEqualTo("Child 2 Description");
                })
                .verifyComplete();
    }
}