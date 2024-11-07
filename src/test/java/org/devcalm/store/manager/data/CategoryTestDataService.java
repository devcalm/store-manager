package org.devcalm.store.manager.data;

import org.devcalm.store.manager.domain.model.Category;
import org.devcalm.store.manager.domain.repository.CategoryRepository;
import org.instancio.Instancio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@Component
public class CategoryTestDataService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory() {
        return categoryRepository.save(Instancio.of(Category.class)
                        .set(field(Category::isArchived), false)
                        .create())
                .block();
    }

    public List<Category> createCategories(int sizeOfElements) {
        var entities = Instancio.ofList(Category.class)
                .size(sizeOfElements)
                .set(field(Category::isArchived), false)
                .create();

        return categoryRepository.saveAll(entities).collectList().block();
    }

    public Category createCategoryWithParent() {
        var parent = categoryRepository.save(Instancio.of(Category.class)
                .ignore(field(Category::getParentId))
                .set(field(Category::isArchived), false)
                .create()).block();

        assertThat(parent).isNotNull();

        return categoryRepository.save(Instancio.of(Category.class)
                        .set(field(Category::getParentId), parent.getId())
                        .set(field(Category::isArchived), false)
                        .create())
                .block();
    }
}
