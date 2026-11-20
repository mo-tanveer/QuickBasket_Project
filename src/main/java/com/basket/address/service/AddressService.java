package com.basket.address.service;

import com.basket.address.dto.AddressRequest;
import com.basket.address.dto.AddressResponse;
import com.basket.address.entity.Address;
import com.basket.address.repository.AddressRepository;
import com.basket.auth.entity.User;
import com.basket.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepo;

    public AddressService(AddressRepository addressRepo) {
        this.addressRepo = addressRepo;
    }

    public List<AddressResponse> getAll(User user) {
        return addressRepo.findByUserIdOrderByDefaultAddressDescIdAsc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AddressResponse add(User user, AddressRequest req) {
        Address a = new Address();
        a.setUser(user);
        apply(a, req);

        boolean isFirst = addressRepo.countByUserId(user.getId()) == 0;

        if (isFirst) {
            a.setDefaultAddress(true);           // pehla address hamesha default
        } else if (req.setAsDefault()) {
            clearDefault(user.getId());          // purana default hatao
            a.setDefaultAddress(true);
        }

        return toResponse(addressRepo.save(a));
    }

    @Transactional
    public AddressResponse update(User user, Long id, AddressRequest req) {
        Address a = findOwned(user, id);
        apply(a, req);

        if (req.setAsDefault() && !a.isDefaultAddress()) {
            clearDefault(user.getId());
            a.setDefaultAddress(true);
        }

        return toResponse(addressRepo.save(a));
    }

    @Transactional
    public AddressResponse setDefault(User user, Long id) {
        Address a = findOwned(user, id);

        if (!a.isDefaultAddress()) {
            clearDefault(user.getId());
            a.setDefaultAddress(true);
            addressRepo.save(a);
        }
        return toResponse(a);
    }

    @Transactional
    public void delete(User user, Long id) {
        Address a = findOwned(user, id);
        boolean wasDefault = a.isDefaultAddress();

        addressRepo.delete(a);

        // default delete hua to koi aur address default bana do
        if (wasDefault) {
            addressRepo.findByUserIdOrderByDefaultAddressDescIdAsc(user.getId())
                    .stream()
                    .findFirst()
                    .ifPresent(next -> next.setDefaultAddress(true));
        }
    }

    // ---------- helpers (Order module mein bhi kaam aayenge) ----------

    public Address findOwned(User user, Long id) {
        return addressRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    private void clearDefault(Long userId) {
        addressRepo.findByUserIdAndDefaultAddressTrue(userId)
                .ifPresent(old -> old.setDefaultAddress(false));
    }

    private void apply(Address a, AddressRequest r) {
        a.setLabel(r.label());
        a.setFullName(r.fullName().trim());
        a.setPhone(r.phone());
        a.setLine1(r.line1().trim());
        a.setLine2(r.line2());
        a.setCity(r.city().trim());
        a.setState(r.state().trim());
        a.setPincode(r.pincode());
    }

    private AddressResponse toResponse(Address a) {
        return new AddressResponse(a.getId(), a.getLabel(), a.getFullName(), a.getPhone(),
                a.getLine1(), a.getLine2(), a.getCity(), a.getState(), a.getPincode(),
                a.isDefaultAddress());
    }
}