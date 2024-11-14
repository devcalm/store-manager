package org.devcalm.store.manager.infrastructure.reflection;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

@Component
public class FieldNamesExtractor {

    public List<String> extract(Class<?> clazz) {
        return Stream.concat(Stream.of(clazz.getDeclaredFields()),
                        Stream.of(clazz.getSuperclass().getDeclaredFields()))
                .map(Field::getName)
                .toList();
    }
}
