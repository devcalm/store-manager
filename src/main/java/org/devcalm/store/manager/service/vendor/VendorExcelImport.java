package org.devcalm.store.manager.service.vendor;

import lombok.RequiredArgsConstructor;
import org.devcalm.store.manager.domain.exception.StoreException;
import org.devcalm.store.manager.domain.model.Vendor;
import org.devcalm.store.manager.domain.repository.CategoryRepository;
import org.devcalm.store.manager.domain.repository.ProductRepository;
import org.devcalm.store.manager.domain.repository.StoreRepository;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static org.devcalm.store.manager.ConstantsHolder.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class VendorExcelImport {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final VendorExcelImportMapper mapper;

    @Transactional
    public Mono<Void> fileImport(Vendor vendor, InputStream inputStream) {
        return Mono.using(
                () -> new ReadableWorkbook(inputStream),
                workbook -> {
                    var categories = mapEntity(getSheet(workbook, CATEGORY_SHEET_NAME), mapper::toCategory);
                    var products = mapEntity(getSheet(workbook, PRODUCT_SHEET_NAME), mapper::toProduct);
                    var categoryProducts = mapperCategory(getSheet(workbook, CATEGORY_PRODUCT_SHEET_NAME), mapper::populateCategoryProducts);
                    var storesCategory = mapperCategory(getSheet(workbook, STORE_SHEET_NAME), mapper::populateStoreProducts);

                    categories.forEach(c -> c.setVendorId(vendor.getId()));
                    products.forEach(p -> p.setVendorId(vendor.getId()));
                    storesCategory.forEach((key, value) -> key.setVendorId(vendor.getId()));

                    return categoryRepository.saveAll(categories).map(category -> {
                                categoryProducts.get(category.getName())
                                        .forEach(s -> products.stream()
                                                .filter(p -> p.getName().equals(s))
                                                .forEach(p -> p.getCategoryIds().add(category.getId())));

                                storesCategory.forEach((k, v) -> v.stream()
                                        .filter(c -> c.equals(category.getName()))
                                        .findFirst()
                                        .ifPresent(c -> k.getCategoryIds().add(category.getId())));
                                return category;
                            })
                            .thenMany(productRepository.saveAll(products))
                            .thenMany(storeRepository.saveAll(storesCategory.keySet()))
                            .then();
                },
                readableWorkbook -> {
                    try {
                        readableWorkbook.close();
                    } catch (IOException e) {
                        throw new StoreException("Error cleaning up excel file", e);
                    }
                }
        );
    }

    private <R> List<R> mapEntity(Sheet sheet, Function<Row, R> mapper) {
        try (var rows = sheet.openStream()) {
            return rows.skip(1).map(mapper).toList();
        } catch (IOException e) {
            throw new StoreException("Cannot open sheet: " + sheet.getName(), e);
        }
    }

    private <T> SequencedMap<T, Set<String>> mapperCategory(Sheet sheet, BiConsumer<Row, SequencedMap<T, Set<String>>> consumer) {
        var map = new LinkedHashMap<T, Set<String>>();
        try (var rows = sheet.openStream()) {
            rows.skip(1).forEach(row -> consumer.accept(row, map));
            return map;
        } catch (IOException e) {
            throw new StoreException("Cannot open sheet: " + sheet.getName(), e);
        }
    }

    private Sheet getSheet(ReadableWorkbook workbook, String sheetName) {
        return workbook.findSheet(sheetName)
                .orElseThrow(throwException("%s sheet is not found.".formatted(sheetName)));
    }

    private Supplier<StoreException> throwException(String message) {
        return () -> new StoreException(message);
    }
}
