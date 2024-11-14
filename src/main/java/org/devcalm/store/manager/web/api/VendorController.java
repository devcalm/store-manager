package org.devcalm.store.manager.web.api;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.devcalm.store.manager.service.vendor.VendorService;
import org.devcalm.store.manager.web.dto.SaveVendorRequest;
import org.devcalm.store.manager.web.dto.VendorDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/vendor")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<VendorDto> create(@RequestBody @Validated SaveVendorRequest request) {
        return vendorService.create(request);
    }


    @GetMapping("{id}")
    public Mono<VendorDto> find(@PathVariable ObjectId id) {
        return vendorService.findById(id);
    }
}
