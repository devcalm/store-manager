package org.devcalm.store.manager.web.api;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.service.vendor.VendorExcelImport;
import org.devcalm.store.manager.service.vendor.VendorFetcher;
import org.devcalm.store.manager.service.vendor.VendorService;
import org.devcalm.store.manager.web.dto.SaveVendorRequest;
import org.devcalm.store.manager.web.dto.VendorDto;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/vendor")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;
    private final VendorFetcher vendorFetcher;
    private final VendorExcelImport vendorExcelImport;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<VendorDto> create(@RequestBody @Validated SaveVendorRequest request) {
        return vendorService.create(request);
    }

    @PutMapping("{id}")
    public Mono<VendorDto> update(@PathVariable ObjectId id, @RequestBody @Validated SaveVendorRequest request) {
        return vendorService.update(id, request);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable ObjectId id) {
        return vendorService.delete(id);
    }

    @GetMapping("{id}")
    public Mono<VendorDto> find(@PathVariable ObjectId id) {
        return vendorService.findById(id);
    }

    @GetMapping
    public Mono<Page<VendorDto>> findAll(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "25") int size,
                                        @RequestParam(defaultValue = "ID") SortField sortField,
                                        @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection) {
        var pageable = PageRequest.of(page, size, sortDirection, sortField.getDatabaseFileName());
        return vendorService.findAll(pageable);
    }

    @PostMapping("{id}/import/excel")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Void> excelImport(@PathVariable("id") ObjectId id, @RequestPart("file") Mono<FilePart> filePartMono) {
        return Mono.zip(vendorFetcher.findById(id), filePartMono).flatMap(tuple -> {
            var vendor = tuple.getT1();
            var filePart = tuple.getT2();
            return DataBufferUtils.join(filePart.content())
                    .flatMap(dataBuffer -> vendorExcelImport.fileImport(vendor, dataBuffer.asInputStream()));
        });
    }
}
