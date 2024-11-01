package org.devcalm.store.manager.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.infrastructure.serializer.ObjectIdSerializer;

import java.util.*;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class CategoryNode {
    @JsonSerialize(using = ObjectIdSerializer.class)
    private final ObjectId id;
    private final String name;
    private final String description;
    private final Set<CategoryNode> children = new TreeSet<>(Comparator.comparing(CategoryNode::getName));

    public void addChild(CategoryNode child) {
        this.children.add(child);
    }
}
