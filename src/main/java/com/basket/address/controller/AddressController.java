package com.basket.address.controller;

import com.basket.address.dto.AddressRequest;
import com.basket.address.dto.AddressResponse;
import com.basket.address.service.AddressService;
import com.basket.auth.entity.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public List<AddressResponse> getAll(@AuthenticationPrincipal User user) {
        return addressService.getAll(user);
    }

    @PostMapping
    public ResponseEntity<AddressResponse> add(@AuthenticationPrincipal User user,
                                               @Valid @RequestBody AddressRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.add(user, req));
    }

    @PutMapping("/{id}")
    public AddressResponse update(@AuthenticationPrincipal User user,
                                  @PathVariable Long id,
                                  @Valid @RequestBody AddressRequest req) {
        return addressService.update(user, id, req);
    }

    @PatchMapping("/{id}/default")
    public AddressResponse setDefault(@AuthenticationPrincipal User user,
                                      @PathVariable Long id) {
        return addressService.setDefault(user, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user,
                                       @PathVariable Long id) {
        addressService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}