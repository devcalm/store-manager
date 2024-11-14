package org.devcalm.store.manager.domain.repository;

import org.bson.types.ObjectId;
import org.devcalm.store.manager.domain.model.Vendor;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface VendorRepository extends ReactiveMongoRepository<Vendor, ObjectId> {
}
