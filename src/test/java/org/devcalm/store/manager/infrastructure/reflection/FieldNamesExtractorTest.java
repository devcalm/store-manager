package org.devcalm.store.manager.infrastructure.reflection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FieldNamesExtractorTest {

    private final FieldNamesExtractor extractor = new FieldNamesExtractor();

    @Test
    void shouldExtract() {
        assertThat(extractor.extract(Child.class)).isEqualTo(List.of("childField", "parentField"));
    }

    private abstract static class Parent {
        private String parentField;
    }
    private static class Child extends Parent {
        private String childField;
    }
}