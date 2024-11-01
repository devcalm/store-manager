package org.devcalm.store.manager.domain.repository.projection;

import org.bson.types.ObjectId;

import java.util.List;

public record IdsProjection(List<ObjectId> ids) {
}
