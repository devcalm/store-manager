package org.devcalm.store.manager.infrastructure.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.exception.StoreException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ObjectIdListSerializer extends JsonSerializer<List<ObjectId>> {

    @Override
    public void serialize(List<ObjectId> objectIds, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartArray();
        Optional.ofNullable(objectIds)
                .orElse(List.of())
                .stream()
                .filter(Objects::nonNull)
                .forEach(objectId -> {
                    try {
                        gen.writeString(objectId.toHexString());
                    } catch (IOException e) {
                        throw new StoreException("Cannot serialized objectId: " + objectId);
                    }
                });
        gen.writeEndArray();
    }
}
