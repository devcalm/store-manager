package org.devcalm.store.manager.data;

import org.devcalm.store.manager.domain.model.Store;
import org.devcalm.store.manager.domain.repository.StoreRepository;
import org.instancio.Instancio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.instancio.Select.field;

@Component
public class StoreTestDataService {

    @Autowired
    private StoreRepository storeRepository;

    public Store createStore() {
        return storeRepository.save(Instancio.of(Store.class)
                .set(field(Store::isArchived), false)
                .create()).block();
    }

    public List<Store> createStores(int sizeOfElements) {
        var entities = Instancio.ofList(Store.class)
                .size(sizeOfElements)
                .set(field(Store::isArchived), false)
                .create();
        return storeRepository.saveAll(entities).collectList().block();
    }
}