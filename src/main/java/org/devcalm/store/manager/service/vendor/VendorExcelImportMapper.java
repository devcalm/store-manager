package org.devcalm.store.manager.service.vendor;

import lombok.RequiredArgsConstructor;
import org.devcalm.store.manager.domain.exception.StoreException;
import org.devcalm.store.manager.domain.model.*;
import org.dhatim.fastexcel.reader.Row;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class VendorExcelImportMapper {

    private final VendorLocationParser vendorLocationParser;

    public Category toCategory(Row row) {
        return Category.builder()
                .name(row.getCellAsString(0).orElseThrow(throwException("Category name is required.")))
                .description(row.getCellAsString(1).orElseThrow(throwException("Category description is required.")))
                .build();
    }

    public Product toProduct(Row row) {
        return Product.builder()
                .name(row.getCellAsString(0).orElseThrow(throwException("Product name is required.")))
                .price(row.getCellAsNumber(1).orElseThrow(throwException("Product price is required.")))
                .timeTakenInMinutes(row.getCellAsNumber(2).orElseThrow(throwException("Product time taken in minutes is required.")).intValue())
                .discount(row.getCellAsNumber(3).orElse(BigDecimal.ZERO).doubleValue())
                .description(row.getCellAsString(4).orElse(null))
                .notes(row.getCellAsString(5).orElse(null))
                .build();
    }

    public Store toStore(Row row) {
        return Store.builder()
                .name(row.getCellAsString(0).orElseThrow(throwException("Store name is required.")))
                .contactInfo(new ContactInfo(
                        row.getCellAsString(1).orElseThrow(throwException("Store email is required.")),
                        row.getCellAsString(2).orElseThrow(throwException("Store phone is required."))
                ))
                .addressInfo(vendorLocationParser.parse(row.getCellAsString(3).orElseThrow(throwException("Store location is required."))))
                .description(row.getCellAsString(4).orElseThrow(throwException("Store description is required.")))
                .build();
    }

    public void populateCategoryProducts(Row row, SequencedMap<String, Set<String>> map) {
        var index = row.getCellAsString(0).orElse(null);
        var mapValue = row.getCellAsString(1).orElseThrow(throwException("Category-Product mapper value is required."));

        if (index != null) {
            var value = new HashSet<String>();
            value.add(mapValue);
            map.put(index, value);
        } else {
            map.lastEntry().getValue().add(mapValue);
        }
    }

    public void populateStoreProducts(Row row, SequencedMap<Store, Set<String>> map) {
        var optionalStoreCell = row.getOptionalCell(0);
        var mapValue = row.getCellAsString(5).orElseThrow(throwException("Store Category Mapper value is required."));

        if (optionalStoreCell.isPresent()) {
            var value = new HashSet<String>();
            value.add(mapValue);
            map.put(toStore(row), value);
        } else {
            map.lastEntry().getValue().add(mapValue);
        }
    }

    private Supplier<StoreException> throwException(String message) {
        return () -> new StoreException(message);
    }
}
